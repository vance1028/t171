package com.market.scale.controller;

import com.market.scale.common.ApiResult;
import com.market.scale.entity.Scale;
import com.market.scale.entity.VerificationRecord;
import com.market.scale.mapper.ScaleMapper;
import com.market.scale.mapper.ScaleStatusLogMapper;
import com.market.scale.mapper.VerificationRecordMapper;
import com.market.scale.security.RequireRole;
import com.market.scale.service.ScaleService;
import com.market.scale.service.ScaleStatusService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/ledger")
@RequireRole
public class LedgerController {

    private final ScaleMapper scaleMapper;
    private final ScaleService scaleService;
    private final ScaleStatusService statusService;
    private final VerificationRecordMapper recordMapper;
    private final ScaleStatusLogMapper statusLogMapper;

    public LedgerController(ScaleMapper scaleMapper, ScaleService scaleService,
                            ScaleStatusService statusService, VerificationRecordMapper recordMapper,
                            ScaleStatusLogMapper statusLogMapper) {
        this.scaleMapper = scaleMapper;
        this.scaleService = scaleService;
        this.statusService = statusService;
        this.recordMapper = recordMapper;
        this.statusLogMapper = statusLogMapper;
    }

    @GetMapping
    public Map<String, Object> ledger(@RequestParam(required = false) Long stallId,
                                      @RequestParam(required = false) String status,
                                      @RequestParam(required = false) String marketName,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "20") int size) {
        Map<String, Object> searchResult = scaleService.search(stallId, status, marketName, page, size);
        @SuppressWarnings("unchecked")
        List<Scale> scales = (List<Scale>) searchResult.get("items");

        List<Map<String, Object>> ledgerItems = new ArrayList<>();
        for (Scale scale : scales) {
            Map<String, Object> item = buildLedgerItem(scale);
            ledgerItems.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("items", ledgerItems);
        result.put("total", searchResult.get("total"));
        result.put("page", searchResult.get("page"));
        result.put("size", searchResult.get("size"));
        return ApiResult.ok(result);
    }

    @GetMapping("/{scaleId}")
    public Map<String, Object> ledgerDetail(@PathVariable Long scaleId) {
        Scale scale = scaleService.get(scaleId);
        Map<String, Object> item = buildLedgerItem(scale);
        List<VerificationRecord> history = recordMapper.findByScaleId(scaleId);
        item.put("historyRecords", history);
        item.put("historyCount", history.size());
        return ApiResult.ok(item);
    }

    private Map<String, Object> buildLedgerItem(Scale scale) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("scaleId", scale.getId());
        item.put("assetNo", scale.getAssetNo());
        item.put("model", scale.getModel());
        item.put("manufacturer", scale.getManufacturer());
        item.put("stallId", scale.getStallId());
        item.put("status", scale.getStatus());
        item.put("verifiedAt", scale.getVerifiedAt());
        item.put("verifyCycleDays", scale.getVerifyCycleDays());
        item.put("nextVerifyDate", scale.getNextVerifyDate());
        item.put("currentSealNo", scale.getCurrentSealNo());

        boolean expired = statusService.isExpired(scale);
        long daysRemaining = statusService.daysUntilNextVerify(scale);
        item.put("inValidity", !expired);
        item.put("daysUntilNextVerify", daysRemaining);

        int totalVerifyCount = recordMapper.countByScaleId(scale.getId());
        item.put("totalVerifyCount", totalVerifyCount);

        int overdueSuspensionCount = statusLogMapper.countOverdueSuspensions(scale.getId());
        item.put("everOverdueUsed", overdueSuspensionCount > 0);

        return item;
    }
}
