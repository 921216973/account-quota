package com.accountquota.facade.impl;

import com.accountquota.config.ThreadContext;
import com.accountquota.enums.InfoStatusEnum;
import com.accountquota.enums.ClientTypeEnum;
import com.accountquota.facade.UserFacade;
import com.accountquota.mybatisplus.entity.ClientDO;
import com.accountquota.mybatisplus.service.ClientMybatisPlusService;
import com.accountquota.vo.req.user.DeductedReq;
import com.accountquota.vo.req.user.SearchReq;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@SpringBootTest
class UserFacadeTest {

    @Autowired
    private UserFacade userFacade;
    @Autowired
    private ClientMybatisPlusService clientService;
    private static Random random = new Random();
    //用户查询用户额度
    @Test
    void search() {
        LambdaQueryWrapper<ClientDO> clientQueryWrapper = new LambdaQueryWrapper<>();
        clientQueryWrapper.eq(ClientDO::getStatus, InfoStatusEnum.NORMAL.getCode());
        clientQueryWrapper.eq(ClientDO::getType, ClientTypeEnum.USER.getCode());
        List<ClientDO> list = clientService.list(clientQueryWrapper);
        for (ClientDO clientDO : list) {
            SearchReq entry = new SearchReq();
            entry.setType(random.nextInt(10));
            ThreadContext.put("clientId", clientDO.getClientId().toString());
            try {
                userFacade.search(entry);
            }catch (Exception e) {

            }

        }
    }
    //用户扣减额度
    @Test
    void deducted() {
        LambdaQueryWrapper<ClientDO> clientQueryWrapper = new LambdaQueryWrapper<>();
        clientQueryWrapper.eq(ClientDO::getStatus, InfoStatusEnum.NORMAL.getCode());
        clientQueryWrapper.eq(ClientDO::getType, ClientTypeEnum.USER.getCode());
        List<ClientDO> list = clientService.list(clientQueryWrapper);
        for (ClientDO clientDO : list) {
            int num = random.nextInt(10);
            for (int i = 0; i < num; i++) {
                DeductedReq deductedReq = new DeductedReq();
                deductedReq.setType(i);
                deductedReq.setQuotaValue(BigDecimal.valueOf(getRandomQuota()));
                ThreadContext.put("clientId", clientDO.getClientId().toString());
                // Run the test
                try {
                    userFacade.deducted(deductedReq);
                } catch (Exception e) {

                }
        }}
    }
    //获取随机100w内数字，小数位数精度6位以内
    private double getRandomQuota(){
        double random = new Random().nextDouble();
        double sp = 0.0 + (random * (1000000.0 - 0.0));
        return Double.parseDouble(String.format("%.6f", sp));
    }
}
