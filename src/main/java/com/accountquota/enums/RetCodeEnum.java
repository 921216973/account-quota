package com.accountquota.enums;

import com.accountquota.mybatisplus.entity.QuotaDO;
import lombok.Getter;

@Getter
public enum RetCodeEnum {
    SUCCESS( 0, "success"),
    ERROR(1, "error"),
    PARAMETER_ERROR(1000, "入参异常"),
    USER_ERROR(2000, "用户信息异常"),
    QUOTA_ERROR(3000, "余额账户异常"),
    ROLE_ERROR(4000, "权限异常"),

    ;

    private final int code;
    private final String desc;

    RetCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
