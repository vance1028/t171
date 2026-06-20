package com.market.scale.controller;

import com.market.scale.common.ApiResult;
import com.market.scale.dto.UserRequest;
import com.market.scale.security.RequireRole;
import com.market.scale.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequireRole({"admin"})
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Map<String, Object> list() {
        return ApiResult.ok(userService.list());
    }

    @PostMapping
    public Map<String, Object> create(@Valid @RequestBody UserRequest req) {
        return ApiResult.ok(userService.create(req));
    }
}
