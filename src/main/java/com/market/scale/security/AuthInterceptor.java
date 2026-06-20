package com.market.scale.security;

import com.market.scale.common.ApiException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public AuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }
        if (requireRole == null) {
            return true; // 公开接口
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw ApiException.unauthorized("缺少或非法的认证令牌");
        }
        String token = header.substring(7).trim();

        Claims claims;
        try {
            claims = jwtUtil.parse(token);
        } catch (Exception e) {
            throw ApiException.unauthorized("令牌无效或已过期");
        }

        Long userId = Long.valueOf(claims.getSubject());
        String username = claims.get("username", String.class);
        String role = claims.get("role", String.class);
        UserContext.set(new CurrentUser(userId, username, role));

        String[] allowed = requireRole.value();
        if (allowed.length > 0 && Arrays.stream(allowed).noneMatch(r -> r.equals(role))) {
            throw ApiException.forbidden("当前角色无权访问该资源");
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
