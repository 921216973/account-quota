package com.accountquota.annotations;

import com.accountquota.enums.ClientTypeEnum;

import java.lang.annotation.*;

/**
 * 自定义角色权限注解
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RoleCheck {
    /**
     * 必传 0 用户 1 业务
     *
     * @return
     */
    ClientTypeEnum role();
}
