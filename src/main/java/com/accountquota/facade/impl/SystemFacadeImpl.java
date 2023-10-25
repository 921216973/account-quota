package com.accountquota.facade.impl;

import com.accountquota.enums.ClientTypeEnum;
import com.accountquota.enums.InfoStatusEnum;
import com.accountquota.facade.SystemFacade;
import com.accountquota.mybatisplus.entity.ClientDO;
import com.accountquota.mybatisplus.service.ClientMybatisPlusService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemFacadeImpl implements SystemFacade {
    @Autowired
    private ClientMybatisPlusService clientService;

    @Override
    public boolean isNormalUser(Integer clientId) {
        LambdaQueryWrapper<ClientDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClientDO::getClientId, clientId);
        queryWrapper.eq(ClientDO::getType, ClientTypeEnum.USER.getCode());
        queryWrapper.eq(ClientDO::getStatus, InfoStatusEnum.NORMAL.getCode());
        ClientDO one = clientService.getOne(queryWrapper);
        if (one == null) {
            return false;
        }
        return true;
    }
}
