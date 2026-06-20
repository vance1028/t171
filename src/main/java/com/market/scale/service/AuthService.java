package com.market.scale.service;

import com.market.scale.common.ApiException;
import com.market.scale.dto.LoginRequest;
import com.market.scale.entity.User;
import com.market.scale.mapper.UserMapper;
import com.market.scale.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Map<String, Object> login(LoginRequest req) {
        User user = userMapper.findByUsername(req.getUsername());
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw ApiException.unauthorized("用户名或密码错误");
        }
        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw ApiException.forbidden("账号已被禁用");
        }
        String token = jwtUtil.generate(user.getId(), user.getUsername(), user.getRole());

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        profile.put("displayName", user.getDisplayName());
        profile.put("role", user.getRole());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", profile);
        return result;
    }
}
