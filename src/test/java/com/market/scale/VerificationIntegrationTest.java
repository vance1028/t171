package com.market.scale;

import com.market.scale.common.ApiException;
import com.market.scale.dto.*;
import com.market.scale.entity.*;
import com.market.scale.mapper.*;
import com.market.scale.service.*;
import com.market.scale.statemachine.ScaleStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VerificationIntegrationTest {

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private ScaleStatusService statusService;

    @Autowired
    private RecheckService recheckService;

    @Autowired
    private ScaleMapper scaleMapper;

    @Autowired
    private StallMapper stallMapper;

    @Autowired
    private VerificationRecordMapper recordMapper;

    @Autowired
    private ScaleStatusLogMapper statusLogMapper;

    private Stall testStall;
    private Scale testScale1;
    private Scale testScale2;
    private Scale testScale3;
    private static final String OPERATOR = "test_inspector";

    @BeforeEach
    void setUp() {
        String uniqueStallNo = "TEST-STALL-" + System.currentTimeMillis();
        testStall = new Stall();
        testStall.setStallNo(uniqueStallNo);
        testStall.setMarketName("测试市场");
        testStall.setMerchantName("测试商户");
        testStall.setStatus("active");
        stallMapper.insert(testStall);
        testStall = stallMapper.findById(testStall.getId());

        cleanUpTestScales();

        testScale1 = createTestScale("TEST-SCALE-1", LocalDate.now().minusDays(100), 365);
        testScale2 = createTestScale("TEST-SCALE-2", LocalDate.now().minusDays(100), 365);
        testScale3 = createTestScale("TEST-SCALE-3", LocalDate.now().minusDays(100), 365);
    }

    private void cleanUpTestScales() {
        for (int i = 1; i <= 3; i++) {
            Scale s = scaleMapper.findByAssetNo("TEST-SCALE-" + i);
            if (s != null) {
                List<ScaleStatusLog> logs = statusLogMapper.findByScaleId(s.getId());
                for (ScaleStatusLog log : logs) {
                    // no delete method, just leave them
                }
                List<VerificationRecord> records = recordMapper.findByScaleId(s.getId());
                // no delete method
                scaleMapper.delete(s.getId());
            }
        }
    }

    private Scale createTestScale(String assetNo, LocalDate verifiedAt, int cycleDays) {
        Scale s = new Scale();
        s.setStallId(testStall.getId());
        s.setAssetNo(assetNo);
        s.setModel("TEST-30");
        s.setManufacturer("测试衡器厂");
        s.setMaxCapacityG(30000);
        s.setVerifiedAt(verifiedAt);
        s.setVerifyCycleDays(cycleDays);
        s.setNextVerifyDate(verifiedAt.plusDays(cycleDays));
        s.setStatus("in_use");
        scaleMapper.insert(s);
        return scaleMapper.findById(s.getId());
    }

    @Test
    void testQualifiedVerification_StatusFlow() {
        Long scaleId = testScale1.getId();

        VerificationPlanRequest planReq = new VerificationPlanRequest();
        planReq.setMarketName("测试市场");
        planReq.setAssignedOrg("测试检定所");
        planReq.setAssignedPerson("张检定");
        planReq.setScaleIds(List.of(scaleId));
        VerificationPlan plan = verificationService.createPlan(planReq, OPERATOR);
        assertNotNull(plan.getId());
        assertEquals("sent", plan.getStatus());

        Scale afterPlan = scaleMapper.findById(scaleId);
        assertEquals(ScaleStatus.PENDING_VERIFY.getCode(), afterPlan.getStatus());

        VerificationRecordRequest recordReq = new VerificationRecordRequest();
        recordReq.setScaleId(scaleId);
        recordReq.setPlanId(plan.getId());
        recordReq.setVerifyOrg("测试检定所");
        recordReq.setVerifyPerson("张检定");
        recordReq.setConclusion("qualified");
        recordReq.setCertNo("CERT-2026-0001");
        recordReq.setSealNo("SEAL-0001");
        recordReq.setVerifiedAt(LocalDate.now());

        VerificationRecord record = verificationService.submitVerification(recordReq, OPERATOR);
        assertNotNull(record.getId());
        assertEquals("qualified", record.getConclusion());
        assertNotNull(record.getNextVerifyDate());
        assertEquals(LocalDate.now().plusDays(365), record.getNextVerifyDate());

        Scale afterVerify = scaleMapper.findById(scaleId);
        assertEquals(ScaleStatus.IN_USE.getCode(), afterVerify.getStatus());
        assertEquals(LocalDate.now(), afterVerify.getVerifiedAt());
        assertEquals(LocalDate.now().plusDays(365), afterVerify.getNextVerifyDate());
        assertEquals("SEAL-0001", afterVerify.getCurrentSealNo());

        List<ScaleStatusLog> logs = statusLogMapper.findByScaleId(scaleId);
        assertTrue(logs.size() >= 3);
    }

    @Test
    void testUnqualifiedVerification_StatusFlow() {
        Long scaleId = testScale2.getId();

        VerificationPlanRequest planReq = new VerificationPlanRequest();
        planReq.setMarketName("测试市场");
        planReq.setScaleIds(List.of(scaleId));
        VerificationPlan plan = verificationService.createPlan(planReq, OPERATOR);

        VerificationRecordRequest recordReq = new VerificationRecordRequest();
        recordReq.setScaleId(scaleId);
        recordReq.setPlanId(plan.getId());
        recordReq.setVerifyOrg("测试检定所");
        recordReq.setConclusion("unqualified");
        recordReq.setVerifiedAt(LocalDate.now());
        recordReq.setRemark("示值超差");

        VerificationRecord record = verificationService.submitVerification(recordReq, OPERATOR);
        assertNotNull(record.getId());
        assertNull(record.getNextVerifyDate());

        Scale afterVerify = scaleMapper.findById(scaleId);
        assertEquals(ScaleStatus.SUSPENDED.getCode(), afterVerify.getStatus());

        RecheckRequest recheckReq = new RecheckRequest();
        recheckReq.setStallId(testStall.getId());
        recheckReq.setCommodity("测试商品");
        recheckReq.setClaimedWeightG(1000);
        recheckReq.setActualWeightG(900);
        ApiException ex = assertThrows(ApiException.class, () -> recheckService.create(recheckReq));
        assertTrue(ex.getMessage().contains("停用") || ex.getMessage().contains("不合格"));
    }

    @Test
    void testLimitedUseVerification_ValidityCalculated() {
        Long scaleId = testScale3.getId();
        int limitedDays = 90;

        VerificationPlanRequest planReq = new VerificationPlanRequest();
        planReq.setMarketName("测试市场");
        planReq.setScaleIds(List.of(scaleId));
        VerificationPlan plan = verificationService.createPlan(planReq, OPERATOR);

        VerificationRecordRequest recordReq = new VerificationRecordRequest();
        recordReq.setScaleId(scaleId);
        recordReq.setPlanId(plan.getId());
        recordReq.setVerifyOrg("测试检定所");
        recordReq.setConclusion("limited_use");
        recordReq.setLimitedUseDays(limitedDays);
        recordReq.setCertNo("CERT-LIMITED-001");
        recordReq.setSealNo("SEAL-LIMITED-001");
        recordReq.setVerifiedAt(LocalDate.now());

        VerificationRecord record = verificationService.submitVerification(recordReq, OPERATOR);
        assertNotNull(record.getId());
        assertEquals("limited_use", record.getConclusion());
        assertEquals(limitedDays, record.getLimitedUseDays());
        assertEquals(LocalDate.now().plusDays(limitedDays), record.getNextVerifyDate());
        assertEquals(LocalDate.now().plusDays(limitedDays), record.getValidUntil());

        Scale afterVerify = scaleMapper.findById(scaleId);
        assertEquals(LocalDate.now().plusDays(limitedDays), afterVerify.getNextVerifyDate());
        assertEquals(ScaleStatus.IN_USE.getCode(), afterVerify.getStatus());
    }

    @Test
    void testLimitedUseWithoutDays_Rejected() {
        Long scaleId = testScale1.getId();

        VerificationPlanRequest planReq = new VerificationPlanRequest();
        planReq.setScaleIds(List.of(scaleId));
        VerificationPlan plan = verificationService.createPlan(planReq, OPERATOR);

        VerificationRecordRequest recordReq = new VerificationRecordRequest();
        recordReq.setScaleId(scaleId);
        recordReq.setPlanId(plan.getId());
        recordReq.setConclusion("limited_use");
        recordReq.setVerifiedAt(LocalDate.now());

        ApiException ex = assertThrows(ApiException.class,
                () -> verificationService.submitVerification(recordReq, OPERATOR));
        assertTrue(ex.getMessage().contains("限用天数"));
    }

    @Test
    void testBatchSubmission_PartialFailure_NoRollback() {
        Long goodId = testScale1.getId();
        Long badId = testScale2.getId();
        Long okId = testScale3.getId();

        VerificationPlanRequest planReq = new VerificationPlanRequest();
        planReq.setScaleIds(List.of(goodId, badId, okId));
        VerificationPlan plan = verificationService.createPlan(planReq, OPERATOR);

        BatchVerificationRequest batchReq = new BatchVerificationRequest();
        batchReq.setPlanId(plan.getId());
        batchReq.setVerifyOrg("测试检定所");
        batchReq.setVerifyPerson("李检定");

        List<BatchVerificationRequest.ItemEntry> items = new ArrayList<>();

        BatchVerificationRequest.ItemEntry good = new BatchVerificationRequest.ItemEntry();
        good.setScaleId(goodId);
        good.setConclusion("qualified");
        good.setCertNo("BATCH-001");
        good.setSealNo("BATCH-SEAL-001");
        good.setVerifiedAt(LocalDate.now());
        items.add(good);

        BatchVerificationRequest.ItemEntry bad = new BatchVerificationRequest.ItemEntry();
        bad.setScaleId(badId);
        bad.setConclusion("invalid_conclusion");
        bad.setVerifiedAt(LocalDate.now());
        items.add(bad);

        BatchVerificationRequest.ItemEntry ok = new BatchVerificationRequest.ItemEntry();
        ok.setScaleId(okId);
        ok.setConclusion("unqualified");
        ok.setVerifiedAt(LocalDate.now());
        items.add(ok);

        batchReq.setItems(items);

        Map<String, Object> result = verificationService.batchSubmitVerification(batchReq, OPERATOR);

        assertEquals(3, result.get("totalSubmitted"));
        assertEquals(2, result.get("successCount"));
        assertEquals(1, result.get("failCount"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> successes = (List<Map<String, Object>>) result.get("successes");
        assertEquals(2, successes.size());

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> failures = (List<Map<String, Object>>) result.get("failures");
        assertEquals(1, failures.size());
        assertEquals(badId, failures.get(0).get("scaleId"));

        Scale goodScale = scaleMapper.findById(goodId);
        assertEquals(ScaleStatus.IN_USE.getCode(), goodScale.getStatus());

        Scale okScale = scaleMapper.findById(okId);
        assertEquals(ScaleStatus.SUSPENDED.getCode(), okScale.getStatus());

        Map<String, Object> planDetail = verificationService.getPlanDetail(plan.getId());
        VerificationPlan updatedPlan = (VerificationPlan) planDetail.get("plan");
        assertEquals("partial", updatedPlan.getStatus());
    }

    @Test
    void testRecheckRejects_VariousInvalidStatuses() {
        Long scaleId = testScale1.getId();

        RecheckRequest recheckReq = new RecheckRequest();
        recheckReq.setStallId(testStall.getId());
        recheckReq.setCommodity("测试商品");
        recheckReq.setClaimedWeightG(1000);
        recheckReq.setActualWeightG(900);

        statusService.transitStatus(scaleId, ScaleStatus.PENDING_VERIFY.getCode(), "测试", OPERATOR);
        statusService.transitStatus(scaleId, ScaleStatus.VERIFYING.getCode(), "测试", OPERATOR);

        Scale verifying = scaleMapper.findById(scaleId);
        assertEquals(ScaleStatus.VERIFYING.getCode(), verifying.getStatus());
        ApiException ex1 = assertThrows(ApiException.class, () -> recheckService.create(recheckReq));
        assertTrue(ex1.getMessage().contains("检定中"));

        statusService.transitStatus(scaleId, ScaleStatus.VERIFIED_PASS.getCode(), "测试", OPERATOR);
        statusService.transitStatus(scaleId, ScaleStatus.IN_USE.getCode(), "测试", OPERATOR);

        Scale expiredScale = scaleMapper.findById(scaleId);
        expiredScale.setNextVerifyDate(LocalDate.now().minusDays(10));
        scaleMapper.update(expiredScale);

        ApiException ex2 = assertThrows(ApiException.class, () -> recheckService.create(recheckReq));
        assertTrue(ex2.getMessage().contains("超过检定有效期"), "Message: " + ex2.getMessage());

        expiredScale.setNextVerifyDate(LocalDate.now().plusDays(365));
        expiredScale.setStatus(ScaleStatus.VERIFIED_FAIL.getCode());
        scaleMapper.update(expiredScale);

        ApiException ex3 = assertThrows(ApiException.class, () -> recheckService.create(recheckReq));
        assertTrue(ex3.getMessage().contains("不合格"), "Message: " + ex3.getMessage());
    }

    @Test
    void testEverOverdueUsed_BasedOnStatusLogs() {
        Long scaleId = testScale1.getId();

        Map<String, Object> ledgerDetail = getLedgerDetail(scaleId);
        assertFalse((Boolean) ledgerDetail.get("everOverdueUsed"));

        statusService.suspendOverdueScale(scaleId, OPERATOR);

        ledgerDetail = getLedgerDetail(scaleId);
        assertTrue((Boolean) ledgerDetail.get("everOverdueUsed"));

        int count = statusLogMapper.countOverdueSuspensions(scaleId);
        assertTrue(count > 0);
    }

    @Test
    void testInvalidConclusion_Rejected() {
        Long scaleId = testScale1.getId();

        VerificationPlanRequest planReq = new VerificationPlanRequest();
        planReq.setScaleIds(List.of(scaleId));
        VerificationPlan plan = verificationService.createPlan(planReq, OPERATOR);

        VerificationRecordRequest recordReq = new VerificationRecordRequest();
        recordReq.setScaleId(scaleId);
        recordReq.setPlanId(plan.getId());
        recordReq.setConclusion("invalid_conclusion");
        recordReq.setVerifiedAt(LocalDate.now());

        ApiException ex = assertThrows(ApiException.class,
                () -> verificationService.submitVerification(recordReq, OPERATOR));
        assertTrue(ex.getMessage().contains("无效的检定结论"));
    }

    @Test
    void testIllegalStateTransition_RejectedByStateMachine() {
        ApiException ex = assertThrows(ApiException.class,
                () -> ScaleStatus.checkTransition(ScaleStatus.IN_USE.getCode(), ScaleStatus.VERIFIED_PASS.getCode()));
        assertTrue(ex.getMessage().contains("不允许从"));

        ex = assertThrows(ApiException.class,
                () -> ScaleStatus.checkTransition(ScaleStatus.SUSPENDED.getCode(), ScaleStatus.IN_USE.getCode()));
        assertTrue(ex.getMessage().contains("不允许从"));

        ex = assertThrows(ApiException.class,
                () -> ScaleStatus.checkTransition(ScaleStatus.VERIFIED_PASS.getCode(), ScaleStatus.SUSPENDED.getCode()));
        assertTrue(ex.getMessage().contains("不允许从"));

        assertDoesNotThrow(() -> ScaleStatus.checkTransition(
                ScaleStatus.IN_USE.getCode(), ScaleStatus.PENDING_VERIFY.getCode()));
        assertDoesNotThrow(() -> ScaleStatus.checkTransition(
                ScaleStatus.PENDING_VERIFY.getCode(), ScaleStatus.VERIFYING.getCode()));
        assertDoesNotThrow(() -> ScaleStatus.checkTransition(
                ScaleStatus.VERIFYING.getCode(), ScaleStatus.VERIFIED_PASS.getCode()));
        assertDoesNotThrow(() -> ScaleStatus.checkTransition(
                ScaleStatus.VERIFIED_PASS.getCode(), ScaleStatus.IN_USE.getCode()));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getLedgerDetail(Long scaleId) {
        Scale scale = scaleMapper.findById(scaleId);
        int totalVerifyCount = recordMapper.countByScaleId(scaleId);
        int overdueCount = statusLogMapper.countOverdueSuspensions(scaleId);

        Map<String, Object> item = new java.util.LinkedHashMap<>();
        item.put("scaleId", scale.getId());
        item.put("assetNo", scale.getAssetNo());
        item.put("inValidity", !statusService.isExpired(scale));
        item.put("daysUntilNextVerify", statusService.daysUntilNextVerify(scale));
        item.put("totalVerifyCount", totalVerifyCount);
        item.put("everOverdueUsed", overdueCount > 0);
        return item;
    }
}
