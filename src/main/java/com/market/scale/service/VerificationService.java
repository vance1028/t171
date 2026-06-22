package com.market.scale.service;

import com.market.scale.common.ApiException;
import com.market.scale.dto.BatchVerificationRequest;
import com.market.scale.dto.VerificationPlanRequest;
import com.market.scale.dto.VerificationRecordRequest;
import com.market.scale.entity.*;
import com.market.scale.mapper.*;
import com.market.scale.statemachine.ScaleStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class VerificationService {

    private static final DateTimeFormatter PLAN_NO_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final AtomicInteger SEQUENCE = new AtomicInteger(0);

    private final VerificationPlanMapper planMapper;
    private final VerificationPlanItemMapper planItemMapper;
    private final VerificationRecordMapper recordMapper;
    private final ScaleMapper scaleMapper;
    private final ScaleStatusService statusService;

    public VerificationService(VerificationPlanMapper planMapper,
                               VerificationPlanItemMapper planItemMapper,
                               VerificationRecordMapper recordMapper,
                               ScaleMapper scaleMapper,
                               ScaleStatusService statusService) {
        this.planMapper = planMapper;
        this.planItemMapper = planItemMapper;
        this.recordMapper = recordMapper;
        this.scaleMapper = scaleMapper;
        this.statusService = statusService;
    }

    @Transactional
    public VerificationPlan createPlan(VerificationPlanRequest req, String operatedBy) {
        List<Long> scaleIds = req.getScaleIds();
        if (scaleIds == null || scaleIds.isEmpty()) {
            throw ApiException.badRequest("请选择至少一台计量器具");
        }

        List<Scale> scales = new ArrayList<>();
        for (Long id : scaleIds) {
            Scale s = scaleMapper.findById(id);
            if (s == null) {
                throw ApiException.notFound("器具ID=" + id + "不存在");
            }
            if (!ScaleStatus.isUsable(s.getStatus()) && !ScaleStatus.PENDING_VERIFY.getCode().equals(s.getStatus())) {
                throw ApiException.badRequest("器具[" + s.getAssetNo() + "]当前状态不允许排入检定计划");
            }
            scales.add(s);
        }

        String planNo = generatePlanNo();
        VerificationPlan plan = new VerificationPlan();
        plan.setPlanNo(planNo);
        plan.setMarketName(req.getMarketName());
        plan.setStatus("draft");
        plan.setTotalCount(scales.size());
        plan.setAssignedOrg(req.getAssignedOrg());
        plan.setAssignedPerson(req.getAssignedPerson());
        plan.setRemark(req.getRemark());
        plan.setCreatedBy(operatedBy);
        planMapper.insert(plan);

        List<VerificationPlanItem> items = new ArrayList<>();
        for (Scale s : scales) {
            VerificationPlanItem item = new VerificationPlanItem();
            item.setPlanId(plan.getId());
            item.setScaleId(s.getId());
            item.setAssetNo(s.getAssetNo());
            item.setResult("pending");
            items.add(item);
        }
        planItemMapper.batchInsert(items);

        for (Scale s : scales) {
            statusService.toPendingVerify(s.getId(), "排入检定计划[" + planNo + "]", operatedBy);
        }

        plan.setStatus("sent");
        planMapper.update(plan);

        return plan;
    }

    @Transactional
    public VerificationPlan createPlanByMarket(String marketName, String assignedOrg, String assignedPerson, String operatedBy) {
        List<Scale> scales = scaleMapper.search(null, "in_use", marketName, 0, 10000);
        if (scales.isEmpty()) {
            throw ApiException.badRequest("该市场没有在用器具");
        }
        VerificationPlanRequest req = new VerificationPlanRequest();
        req.setMarketName(marketName);
        req.setAssignedOrg(assignedOrg);
        req.setAssignedPerson(assignedPerson);
        req.setScaleIds(scales.stream().map(Scale::getId).toList());
        return createPlan(req, operatedBy);
    }

    @Transactional
    public VerificationRecord submitVerification(VerificationRecordRequest req, String operatedBy) {
        Scale scale = scaleMapper.findById(req.getScaleId());
        if (scale == null) {
            throw ApiException.notFound("计量器具不存在");
        }

        String conclusion = req.getConclusion();
        LocalDate verifiedAt = req.getVerifiedAt();
        int cycleDays = scale.getVerifyCycleDays() != null ? scale.getVerifyCycleDays() : 365;

        LocalDate nextVerifyDate;
        LocalDate validUntil;

        if ("qualified".equals(conclusion)) {
            if (req.getLimitedUseDays() != null && req.getLimitedUseDays() > 0) {
                nextVerifyDate = verifiedAt.plusDays(req.getLimitedUseDays());
                validUntil = nextVerifyDate;
            } else {
                nextVerifyDate = verifiedAt.plusDays(cycleDays);
                validUntil = nextVerifyDate;
            }
        } else {
            nextVerifyDate = null;
            validUntil = null;
        }

        VerificationRecord record = new VerificationRecord();
        record.setScaleId(req.getScaleId());
        record.setPlanId(req.getPlanId());
        record.setVerifyOrg(req.getVerifyOrg());
        record.setVerifyPerson(req.getVerifyPerson());
        record.setConclusion(conclusion);
        record.setCertNo(req.getCertNo());
        record.setSealNo(req.getSealNo());
        record.setVerifiedAt(verifiedAt);
        record.setNextVerifyDate(nextVerifyDate);
        record.setValidUntil(validUntil);
        record.setLimitedUseDays(req.getLimitedUseDays());
        record.setRemark(req.getRemark());
        recordMapper.insert(record);

        if ("qualified".equals(conclusion)) {
            statusService.toVerifiedPass(scale.getId(), operatedBy);

            scale.setVerifiedAt(verifiedAt);
            scale.setNextVerifyDate(nextVerifyDate);
            scale.setCurrentSealNo(req.getSealNo());
            scaleMapper.update(scale);

            statusService.toInUse(scale.getId(), operatedBy);
        } else if ("unqualified".equals(conclusion)) {
            statusService.toVerifiedFail(scale.getId(), operatedBy);
            statusService.suspendFailedScale(scale.getId(), operatedBy);
        } else if ("limited_use".equals(conclusion)) {
            statusService.toVerifiedPass(scale.getId(), operatedBy);

            scale.setVerifiedAt(verifiedAt);
            scale.setNextVerifyDate(nextVerifyDate);
            scale.setCurrentSealNo(req.getSealNo());
            scaleMapper.update(scale);

            statusService.toInUse(scale.getId(), operatedBy);
        }

        updatePlanItemResult(req.getPlanId(), req.getScaleId(), conclusion, null);

        return record;
    }

    @Transactional
    public Map<String, Object> batchSubmitVerification(BatchVerificationRequest req, String operatedBy) {
        List<BatchVerificationRequest.ItemEntry> items = req.getItems();
        if (items == null || items.isEmpty()) {
            throw ApiException.badRequest("检定条目不能为空");
        }

        List<Map<String, Object>> successes = new ArrayList<>();
        List<Map<String, Object>> failures = new ArrayList<>();

        for (BatchVerificationRequest.ItemEntry entry : items) {
            try {
                VerificationRecordRequest recordReq = new VerificationRecordRequest();
                recordReq.setScaleId(entry.getScaleId());
                recordReq.setPlanId(req.getPlanId());
                recordReq.setVerifyOrg(req.getVerifyOrg());
                recordReq.setVerifyPerson(req.getVerifyPerson());
                recordReq.setConclusion(entry.getConclusion());
                recordReq.setCertNo(entry.getCertNo());
                recordReq.setSealNo(entry.getSealNo());
                recordReq.setVerifiedAt(entry.getVerifiedAt());
                recordReq.setLimitedUseDays(entry.getLimitedUseDays());
                recordReq.setRemark(entry.getRemark());

                VerificationRecord saved = submitVerification(recordReq, operatedBy);
                Map<String, Object> ok = new HashMap<>();
                ok.put("scaleId", entry.getScaleId());
                ok.put("assetNo", entry.getAssetNo());
                ok.put("recordId", saved.getId());
                ok.put("conclusion", saved.getConclusion());
                successes.add(ok);
            } catch (Exception e) {
                Map<String, Object> fail = new HashMap<>();
                fail.put("scaleId", entry.getScaleId());
                fail.put("assetNo", entry.getAssetNo());
                fail.put("reason", e.getMessage());
                failures.add(fail);
                updatePlanItemResult(req.getPlanId(), entry.getScaleId(), "failed", e.getMessage());
            }
        }

        if (req.getPlanId() != null) {
            updatePlanCompletionStatus(req.getPlanId());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalSubmitted", items.size());
        result.put("successCount", successes.size());
        result.put("failCount", failures.size());
        result.put("successes", successes);
        result.put("failures", failures);
        return result;
    }

    public Map<String, Object> getPlanDetail(Long planId) {
        VerificationPlan plan = planMapper.findById(planId);
        if (plan == null) {
            throw ApiException.notFound("检定计划不存在");
        }
        List<VerificationPlanItem> items = planItemMapper.findByPlanId(planId);
        Map<String, Object> data = new HashMap<>();
        data.put("plan", plan);
        data.put("items", items);
        return data;
    }

    public Map<String, Object> pagePlans(String marketName, String status, int page, int size) {
        int p = Math.max(page, 1);
        int s = Math.min(Math.max(size, 1), 200);
        List<VerificationPlan> rows = planMapper.search(marketName, status, (p - 1) * s, s);
        long total = planMapper.count(marketName, status);
        Map<String, Object> result = new HashMap<>();
        result.put("items", rows);
        result.put("total", total);
        result.put("page", p);
        result.put("size", s);
        return result;
    }

    public Map<String, Object> pageRecords(Long scaleId, String conclusion, int page, int size) {
        int p = Math.max(page, 1);
        int s = Math.min(Math.max(size, 1), 200);
        List<VerificationRecord> rows = recordMapper.search(scaleId, conclusion, (p - 1) * s, s);
        long total = recordMapper.count(scaleId, conclusion);
        Map<String, Object> result = new HashMap<>();
        result.put("items", rows);
        result.put("total", total);
        result.put("page", p);
        result.put("size", s);
        return result;
    }

    public List<Scale> getWarningScales(int warnDays) {
        return scaleMapper.findExpiringSoon(warnDays);
    }

    public List<Scale> getOverdueScales() {
        return scaleMapper.findOverdue();
    }

    private String generatePlanNo() {
        String datePart = LocalDate.now().format(PLAN_NO_FMT);
        int seq = SEQUENCE.incrementAndGet();
        return "VP-" + datePart + "-" + String.format("%04d", seq);
    }

    private void updatePlanItemResult(Long planId, Long scaleId, String result, String failReason) {
        if (planId == null) {
            return;
        }
        List<VerificationPlanItem> items = planItemMapper.findByPlanId(planId);
        for (VerificationPlanItem item : items) {
            if (item.getScaleId().equals(scaleId)) {
                item.setResult(result);
                item.setFailReason(failReason);
                planItemMapper.updateResult(item);
                break;
            }
        }
    }

    private void updatePlanCompletionStatus(Long planId) {
        List<VerificationPlanItem> items = planItemMapper.findByPlanId(planId);
        boolean allDone = true;
        boolean anyFailed = false;
        for (VerificationPlanItem item : items) {
            if ("pending".equals(item.getResult())) {
                allDone = false;
            }
            if ("failed".equals(item.getResult()) || "unqualified".equals(item.getResult())) {
                anyFailed = true;
            }
        }
        VerificationPlan plan = planMapper.findById(planId);
        if (plan == null) {
            return;
        }
        if (allDone) {
            plan.setStatus(anyFailed ? "partial" : "completed");
        } else {
            plan.setStatus("in_progress");
        }
        planMapper.update(plan);
    }
}
