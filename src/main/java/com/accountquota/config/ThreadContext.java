package com.accountquota.config;

import java.util.Map;
import java.util.HashMap;

public class ThreadContext {
    private static final ThreadLocal<Map<String, String>> threadLocal = ThreadLocal.withInitial(HashMap::new);

    public ThreadContext() {
    }

    public static void put(String key, String value) {
        threadLocal.get().put(key, value);
    }

    public static String get(String key) {
        return threadLocal.get().get(key);
    }

    public static void remove() {
        threadLocal.remove();
    }
}
