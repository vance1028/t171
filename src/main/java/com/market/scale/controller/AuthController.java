package com.market.scale.controller;

import com.market.scale.common.ApiResult;
import com.market.scale.dto.LoginRequest;
import com.market.scale.security.CurrentUser;
import com.market.scale.security.RequireRole;
import com.market.scale.security.UserContext;
import com.market.scale.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginRequest req) {
        return ApiResult.ok(authService.login(req));
    }

    @RequireRole
    @GetMapping("/me")
    public Map<String, Object> me() {
        CurrentUser user = UserContext.get();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getUserId());
        data.put("username", user.getUsername());
        data.put("role", user.getRole());
        return ApiResult.ok(data);
    }
}
