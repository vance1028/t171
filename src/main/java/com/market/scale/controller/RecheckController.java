package com.market.scale.controller;

import com.market.scale.common.ApiResult;
import com.market.scale.dto.RecheckRequest;
import com.market.scale.security.RequireRole;
import com.market.scale.service.RecheckService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rechecks")
@RequireRole
public class RecheckController {

    private final RecheckService recheckService;

    public RecheckController(RecheckService recheckService) {
        this.recheckService = recheckService;
    }

    @GetMapping
    public Map<String, Object> page(@RequestParam(required = false) Long stallId,
                                    @RequestParam(required = false) String result,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        return ApiResult.ok(recheckService.page(stallId, result, page, size));
    }

    @PostMapping
    @RequireRole({"admin", "inspector"})
    public Map<String, Object> create(@Valid @RequestBody RecheckRequest req) {
        return ApiResult.ok(recheckService.create(req));
    }
}
