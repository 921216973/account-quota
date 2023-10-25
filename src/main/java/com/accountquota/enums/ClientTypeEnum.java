package com.accountquota.enums;

import lombok.Getter;

@Getter
public enum ClientTypeEnum {

    USER(0, "用户"),
    SALESMAN(1, "业务员");

    private final int code;
    private final String desc;

    ClientTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
