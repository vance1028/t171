package com.market.scale.controller;

import com.market.scale.common.ApiResult;
import com.market.scale.dto.BatchVerificationRequest;
import com.market.scale.dto.VerificationPlanRequest;
import com.market.scale.dto.VerificationRecordRequest;
import com.market.scale.security.CurrentUser;
import com.market.scale.security.RequireRole;
import com.market.scale.security.UserContext;
import com.market.scale.service.VerificationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/verifications")
@RequireRole
public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @GetMapping("/plans")
    public Map<String, Object> listPlans(@RequestParam(required = false) String marketName,
                                         @RequestParam(required = false) String status,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "20") int size) {
        return ApiResult.ok(verificationService.pagePlans(marketName, status, page, size));
    }

    @GetMapping("/plans/{id}")
    public Map<String, Object> getPlanDetail(@PathVariable Long id) {
        return ApiResult.ok(verificationService.getPlanDetail(id));
    }

    @PostMapping("/plans")
    @RequireRole({"admin", "inspector"})
    public Map<String, Object> createPlan(@Valid @RequestBody VerificationPlanRequest req) {
        CurrentUser user = UserContext.get();
        return ApiResult.ok(verificationService.createPlan(req, user.getUsername()));
    }

    @PostMapping("/plans/by-market")
    @RequireRole({"admin", "inspector"})
    public Map<String, Object> createPlanByMarket(@RequestParam String marketName,
                                                   @RequestParam(required = false) String assignedOrg,
                                                   @RequestParam(required = false) String assignedPerson) {
        CurrentUser user = UserContext.get();
        return ApiResult.ok(verificationService.createPlanByMarket(marketName, assignedOrg, assignedPerson, user.getUsername()));
    }

    @PostMapping("/records")
    @RequireRole({"admin", "inspector"})
    public Map<String, Object> submitRecord(@Valid @RequestBody VerificationRecordRequest req) {
        CurrentUser user = UserContext.get();
        return ApiResult.ok(verificationService.submitVerification(req, user.getUsername()));
    }

    @PostMapping("/records/batch")
    @RequireRole({"admin", "inspector"})
    public Map<String, Object> batchSubmitRecords(@Valid @RequestBody BatchVerificationRequest req) {
        CurrentUser user = UserContext.get();
        return ApiResult.ok(verificationService.batchSubmitVerification(req, user.getUsername()));
    }

    @GetMapping("/records")
    public Map<String, Object> listRecords(@RequestParam(required = false) Long scaleId,
                                            @RequestParam(required = false) String conclusion,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        return ApiResult.ok(verificationService.pageRecords(scaleId, conclusion, page, size));
    }

    @GetMapping("/warnings")
    public Map<String, Object> getWarnings(@RequestParam(defaultValue = "30") int warnDays) {
        return ApiResult.ok(verificationService.getWarningScales(warnDays));
    }

    @GetMapping("/overdue")
    public Map<String, Object> getOverdue() {
        return ApiResult.ok(verificationService.getOverdueScales());
    }
}
