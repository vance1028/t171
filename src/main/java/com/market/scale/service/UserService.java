package com.market.scale.service;

import com.market.scale.common.ApiException;
import com.market.scale.dto.UserRequest;
import com.market.scale.entity.User;
import com.market.scale.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private static final Set<String> VALID_ROLES = Set.of("admin", "inspector", "viewer");

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> list() {
        List<User> users = userMapper.findAll();
        users.forEach(u -> u.setPasswordHash(null));
        return users;
    }

    public User create(UserRequest req) {
        if (!VALID_ROLES.contains(req.getRole())) {
            throw ApiException.badRequest("非法角色，仅支持 admin/inspector/viewer");
        }
        if (userMapper.findByUsername(req.getUsername()) != null) {
            throw ApiException.conflict("用户名已存在");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setDisplayName(req.getDisplayName());
        user.setRole(req.getRole());
        user.setEnabled(true);
        userMapper.insert(user);
        user.setPasswordHash(null);
        return user;
    }
}
