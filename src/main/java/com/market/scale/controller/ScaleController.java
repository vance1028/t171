package com.market.scale.controller;

import com.market.scale.common.ApiResult;
import com.market.scale.dto.ScaleRequest;
import com.market.scale.entity.Scale;
import com.market.scale.security.RequireRole;
import com.market.scale.service.ScaleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/scales")
@RequireRole
public class ScaleController {

    private final ScaleService scaleService;

    public ScaleController(ScaleService scaleService) {
        this.scaleService = scaleService;
    }

    @GetMapping
    public Map<String, Object> page(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        return ApiResult.ok(scaleService.page(page, size));
    }

    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable Long id) {
        Scale scale = scaleService.get(id);
        Map<String, Object> data = new HashMap<>();
        data.put("scale", scale);
        data.put("expired", scaleService.isExpired(scale));
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
}
