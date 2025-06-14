package com.tehacko.backend_java.util;

import java.util.Map;

public class RequestUtil {
    public static boolean extractAllowPersistent(Map<String, Object> requestBody) {
        if (requestBody == null) return false;

        Object allowPersistentObj = requestBody.get("allowPersistent");
        if (allowPersistentObj instanceof Boolean) {
            return (Boolean) allowPersistentObj;
        } else if (allowPersistentObj instanceof String) {
            return Boolean.parseBoolean((String) allowPersistentObj);
        }
        return false;
    }
}