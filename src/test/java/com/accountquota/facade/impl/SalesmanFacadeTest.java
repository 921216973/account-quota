package com.accountquota.facade.impl;

import com.accountquota.enums.InfoStatusEnum;
import com.accountquota.enums.ClientTypeEnum;
import com.accountquota.facade.SalesmanFacade;
import com.accountquota.mybatisplus.entity.ClientDO;
import com.accountquota.mybatisplus.service.ClientMybatisPlusService;
import com.accountquota.vo.req.salesman.AdjustReq;
import com.accountquota.vo.req.salesman.InitReq;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class SalesmanFacadeTest {
    @Autowired
    private SalesmanFacade salesmanFacade;
    @Autowired
    private ClientMybatisPlusService clientService;
    private static Random random = new Random();

    //随机创建用户
    @Test
    void createUser() {
        List<ClientDO> userList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            ClientDO clientDO = new ClientDO();
            clientDO.setUsername(UUID.randomUUID().toString().replaceAll("-", ""));
            clientDO.setStatus(random.nextInt(2));
            clientDO.setType(random.nextInt(2));
            userList.add(clientDO);
        }
        clientService.saveBatch(userList);
    }

    //初始化账户
    @Test
    void init() {
        LambdaQueryWrapper<ClientDO> clientQueryWrapper = new LambdaQueryWrapper<>();
        clientQueryWrapper.eq(ClientDO::getStatus, InfoStatusEnum.NORMAL.getCode());
        clientQueryWrapper.eq(ClientDO::getType, ClientTypeEnum.USER.getCode());
        List<ClientDO> list = clientService.list(clientQueryWrapper);
        for (ClientDO clientDO : list) {
            int num = random.nextInt(10);
            for (int i = 0; i < num; i++) {
                InitReq initReq = new InitReq();
                initReq.setClientId(clientDO.getClientId());
                initReq.setType(i);
                initReq.setQuotaValue(BigDecimal.valueOf(getRandomQuota()));
                // Run the test
                try {
                    salesmanFacade.init(initReq);
                } catch (Exception e) {

                }

            }
        }
    }

    //业务调整用户额度
    @Test
    void adjust() {
        LambdaQueryWrapper<ClientDO> clientQueryWrapper = new LambdaQueryWrapper<>();
        clientQueryWrapper.eq(ClientDO::getStatus, InfoStatusEnum.NORMAL.getCode());
        clientQueryWrapper.eq(ClientDO::getType, ClientTypeEnum.USER.getCode());
        List<ClientDO> list = clientService.list(clientQueryWrapper);
        for (ClientDO clientDO : list) {
            int num = random.nextInt(10);
            for (int i = 0; i < num; i++) {
                AdjustReq adjustReq = new AdjustReq();
                adjustReq.setClientId(clientDO.getClientId());
                adjustReq.setAdjustType(random.nextInt(2));
                adjustReq.setQuotaValue(BigDecimal.valueOf(getRandomQuota()));
                adjustReq.setAccountType(i);
                // Run the test
                try {
                    salesmanFacade.adjust(adjustReq);
                } catch (Exception e) {

                }

            }
        }
    }

    //获取随机100w内数字，小数位数精度6位以内
    private double getRandomQuota() {
        double random = new Random().nextDouble();
        double sp = 0.0 + (random * (1000000.0 - 0.0));
        return Double.parseDouble(String.format("%.6f", sp));
    }

}