package com.market.scale;

import com.market.scale.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil(
            "fair-scale-market-metrology-supervision-secret-key-for-unit-test-only", 60);

    @Test
    void generateAndParseRoundTrip() {
        String token = jwtUtil.generate(7L, "inspector", "inspector");
        Claims claims = jwtUtil.parse(token);
        assertEquals("7", claims.getSubject());
        assertEquals("inspector", claims.get("username", String.class));
        assertEquals("inspector", claims.get("role", String.class));
    }

    @Test
    void tamperedTokenRejected() {
        String token = jwtUtil.generate(1L, "admin", "admin");
        String tampered = token.substring(0, token.length() - 2) + "xx";
        assertThrows(Exception.class, () -> jwtUtil.parse(tampered));
    }
}
