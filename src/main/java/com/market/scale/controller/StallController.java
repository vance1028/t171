package com.market.scale.controller;

import com.market.scale.common.ApiResult;
import com.market.scale.dto.StallRequest;
import com.market.scale.security.RequireRole;
import com.market.scale.service.StallService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/stalls")
@RequireRole
public class StallController {

    private final StallService stallService;

    public StallController(StallService stallService) {
        this.stallService = stallService;
    }

    @GetMapping
    public Map<String, Object> page(@RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) String status,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        return ApiResult.ok(stallService.page(keyword, status, page, size));
    }

    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable Long id) {
        return ApiResult.ok(stallService.get(id));
    }

    @PostMapping
    @RequireRole({"admin", "inspector"})
    public Map<String, Object> create(@Valid @RequestBody StallRequest req) {
        return ApiResult.ok(stallService.create(req));
    }

    @PutMapping("/{id}")
    @RequireRole({"admin", "inspector"})
    public Map<String, Object> update(@PathVariable Long id, @Valid @RequestBody StallRequest req) {
        return ApiResult.ok(stallService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @RequireRole({"admin"})
    public Map<String, Object> delete(@PathVariable Long id) {
        stallService.delete(id);
        return ApiResult.ok();
    }
}
