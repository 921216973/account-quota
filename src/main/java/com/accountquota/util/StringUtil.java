package com.accountquota.util;


public class StringUtil {

    /**
     * 判断字符串是否为null或""
     *
     * @param str 字符串
     * @return 若为null或""返回true，反之false
     */
    public static Boolean isBlank(String str) {
        return str == null || str.trim().isEmpty() || "null".equalsIgnoreCase(str);
    }

    public static Boolean notBlank(String str) {
        return str != null && !str.trim().isEmpty() && !"null".equalsIgnoreCase(str);
    }
}
