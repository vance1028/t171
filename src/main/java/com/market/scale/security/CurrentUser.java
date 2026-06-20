package com.market.scale.security;

/**
 * 当前登录用户上下文，由拦截器解析 JWT 后写入。
 */
public class CurrentUser {
    private final Long userId;
    private final String username;
    private final String role;

    public CurrentUser(Long userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}
