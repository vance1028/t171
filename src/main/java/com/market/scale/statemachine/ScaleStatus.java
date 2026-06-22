package com.market.scale.statemachine;

import com.market.scale.common.ApiException;

import java.util.Map;
import java.util.Set;

public enum ScaleStatus {

    IN_USE("in_use"),
    SUSPENDED("suspended"),
    PENDING_VERIFY("pending_verify"),
    VERIFYING("verifying"),
    VERIFIED_PASS("verified_pass"),
    VERIFIED_FAIL("verified_fail");

    private final String code;

    private static final Map<ScaleStatus, Set<ScaleStatus>> TRANSITIONS = Map.of(
            IN_USE, Set.of(PENDING_VERIFY, SUSPENDED),
            SUSPENDED, Set.of(PENDING_VERIFY),
            PENDING_VERIFY, Set.of(VERIFYING, VERIFIED_PASS, VERIFIED_FAIL, SUSPENDED),
            VERIFYING, Set.of(VERIFIED_PASS, VERIFIED_FAIL),
            VERIFIED_PASS, Set.of(IN_USE),
            VERIFIED_FAIL, Set.of(SUSPENDED)
    );

    ScaleStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static ScaleStatus fromCode(String code) {
        for (ScaleStatus s : values()) {
            if (s.code.equals(code)) {
                return s;
            }
        }
        throw ApiException.badRequest("无效的器具状态: " + code);
    }

    public boolean canTransitTo(ScaleStatus target) {
        Set<ScaleStatus> allowed = TRANSITIONS.get(this);
        return allowed != null && allowed.contains(target);
    }

    public static void checkTransition(String fromCode, String toCode) {
        if (fromCode == null) {
            return;
        }
        ScaleStatus from = fromCode(fromCode);
        ScaleStatus to = fromCode(toCode);
        if (!from.canTransitTo(to)) {
            throw ApiException.badRequest(
                    "器具状态不允许从 [" + from.code + "] 变更为 [" + to.code + "]");
        }
    }

    public static boolean isActive(String code) {
        return IN_USE.code.equals(code) || VERIFIED_PASS.code.equals(code);
    }

    public static boolean isUsable(String code) {
        return IN_USE.code.equals(code);
    }

    public static boolean isSuspended(String code) {
        return SUSPENDED.code.equals(code);
    }
}
