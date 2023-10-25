package com.accountquota.enums;

import lombok.Getter;

@Getter
public enum AdjustTypeEnum {
    SUBTRACT(0, "减少额度"),
    ADD(1, "增加额度"),
    ;

    private final int code;
    private final String desc;

    AdjustTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
