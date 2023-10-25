package com.accountquota.config;

import com.accountquota.bean.BaseResult;
import com.accountquota.enums.InfoStatusEnum;
import com.accountquota.enums.RetCodeEnum;
import com.accountquota.mybatisplus.entity.ClientDO;
import com.accountquota.mybatisplus.service.ClientMybatisPlusService;
import com.accountquota.util.HttpServletUtil;
import com.accountquota.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    @Autowired
    private ClientMybatisPlusService clientService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String username = request.getHeader("username");
        if (StringUtil.notBlank(username)) {
            LambdaQueryWrapper<ClientDO> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ClientDO::getUsername, username);
            wrapper.eq(ClientDO::getStatus, InfoStatusEnum.NORMAL.getCode());
            ClientDO client = clientService.getOne(wrapper);
            if (client != null) {
                ThreadContext.put("clientId",client.getClientId().toString());
                ThreadContext.put("type",client.getType().toString());
                return true;
            }
        }
        return loginErrorMsg(response);
    }

    private boolean loginErrorMsg(HttpServletResponse response) {
        response.setStatus(403);
        BaseResult baseRes = new BaseResult();
        baseRes.setRetCode(RetCodeEnum.USER_ERROR.getCode());
        baseRes.setMessage("用户未登录");
        HttpServletUtil.responseOutWithJson(response, baseRes);
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }
}
