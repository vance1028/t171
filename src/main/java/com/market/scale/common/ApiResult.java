package com.market.scale.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一响应体。code=0 表示成功，非 0 表示业务/系统错误。
 */
public class ApiResult {

    public static Map<String, Object> ok(Object data) {
        Map<String, Object> m = new HashMap<>();
        m.put("code", 0);
        m.put("message", "ok");
        m.put("data", data);
        return m;
    }

    public static Map<String, Object> ok() {
        return ok(null);
    }

    public static Map<String, Object> error(int code, String message) {
        Map<String, Object> m = new HashMap<>();
        m.put("code", code);
        m.put("message", message);
        m.put("data", null);
        return m;
    }
}
