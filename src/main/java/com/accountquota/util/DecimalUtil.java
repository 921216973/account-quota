package com.accountquota.util;

import java.math.BigDecimal;

public class DecimalUtil {
    /**
     * 检查额度数据是否满足要求 整数位12位以内 小数位6位以内
     */
    public static boolean checkDecimal(BigDecimal value) {
        String numberString = value.stripTrailingZeros().toPlainString();
        if (!numberString.contains(".")) {
            return numberString.length() < 12;
        }
        String[] split = numberString.split("\\.");
        if (split[0].length() < 12) {
            return split[1].length() < 7;
        } else {
            return false;
        }
    }
}
