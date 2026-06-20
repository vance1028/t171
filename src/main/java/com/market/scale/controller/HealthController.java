package com.market.scale.controller;

import com.market.scale.common.ApiResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public Map<String, Object> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "up");
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            data.put("db", "ok");
        } catch (Exception e) {
            data.put("db", "down");
        }
        return ApiResult.ok(data);
    }
}
