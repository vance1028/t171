package com.market.scale.controller;

import com.market.scale.common.ApiResult;
import com.market.scale.dto.ScaleRequest;
import com.market.scale.dto.StatusChangeRequest;
import com.market.scale.entity.Scale;
import com.market.scale.security.RequireRole;
import com.market.scale.service.ScaleService;
import com.market.scale.service.ScaleStatusService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/scales")
@RequireRole
public class ScaleController {

    private final ScaleService scaleService;
    private final ScaleStatusService statusService;

    public ScaleController(ScaleService scaleService, ScaleStatusService statusService) {
        this.scaleService = scaleService;
        this.statusService = statusService;
    }

    @GetMapping
    public Map<String, Object> page(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        return ApiResult.ok(scaleService.page(page, size));
    }

    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam(required = false) Long stallId,
                                       @RequestParam(required = false) String status,
                                       @RequestParam(required = false) String marketName,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "20") int size) {
        return ApiResult.ok(scaleService.search(stallId, status, marketName, page, size));
    }

    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable Long id) {
        Scale scale = scaleService.get(id);
        Map<String, Object> data = new HashMap<>();
        data.put("scale", scale);
        data.put("expired", scaleService.isExpired(scale));
        data.put("daysUntilNextVerify", scaleService.daysUntilNextVerify(scale));
        return ApiResult.ok(data);
    }

    @PostMapping
    @RequireRole({"admin", "inspector"})
    public Map<String, Object> create(@Valid @RequestBody ScaleRequest req) {
        return ApiResult.ok(scaleService.create(req));
    }

    @PutMapping("/{id}")
    @RequireRole({"admin", "inspector"})
    public Map<String, Object> update(@PathVariable Long id, @Valid @RequestBody ScaleRequest req) {
        return ApiResult.ok(scaleService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @RequireRole({"admin"})
    public Map<String, Object> delete(@PathVariable Long id) {
        scaleService.delete(id);
        return ApiResult.ok();
    }

    @PutMapping("/{id}/status")
    @RequireRole({"admin", "inspector"})
    public Map<String, Object> changeStatus(@PathVariable Long id,
                                             @RequestParam String targetStatus,
                                             @RequestBody(required = false) StatusChangeRequest req) {
        String reason = (req != null && req.getReason() != null) ? req.getReason() : "手动变更状态";
        com.market.scale.security.CurrentUser user = com.market.scale.security.UserContext.get();
        Scale updated = statusService.transitStatus(id, targetStatus, reason, user.getUsername());
        return ApiResult.ok(updated);
    }

    @GetMapping("/{id}/status-logs")
    public Map<String, Object> statusLogs(@PathVariable Long id,
                                           @RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        return ApiResult.ok(statusService.getStatusLogs(id, page, size));
    }
}
