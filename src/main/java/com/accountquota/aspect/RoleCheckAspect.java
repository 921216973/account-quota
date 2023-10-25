package com.accountquota.aspect;

import com.accountquota.annotations.RoleCheck;
import com.accountquota.config.ThreadContext;
import com.accountquota.enums.ClientTypeEnum;
import com.accountquota.enums.RetCodeEnum;
import com.accountquota.exception.ScException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RoleCheckAspect {
    private static Logger logger = LoggerFactory.getLogger(RoleCheckAspect.class);

    @Pointcut("@annotation(com.accountquota.annotations.RoleCheck)")
    public void roleCheckCut() {
    }

    @Before("roleCheckCut() && @annotation(roleCheck)")
    public void doBefore(RoleCheck roleCheck) {
        //判断注解传参和当前登录用户类型是否相同
        int role = roleCheck.role().getCode();
        String type = ThreadContext.get("type");
        if (!String.valueOf(role).equals(type)) {
            throw new ScException(RetCodeEnum.ROLE_ERROR.getCode(), RetCodeEnum.ROLE_ERROR.getDesc());
        }
    }


}
