package com.accountquota.enums;

import lombok.Getter;

@Getter
public enum InfoStatusEnum {
    NORMAL(1, "正常"),
    DISABLE(0, "停用");

    private final int code;
    private final String desc;

    InfoStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
