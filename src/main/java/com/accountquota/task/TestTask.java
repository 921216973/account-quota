package com.accountquota.task;

import com.accountquota.enums.ClientTypeEnum;
import com.accountquota.enums.InfoStatusEnum;
import com.accountquota.mybatisplus.entity.ClientDO;
import com.accountquota.mybatisplus.service.ClientMybatisPlusService;
import com.accountquota.thread.AdjustThread;
import com.accountquota.thread.DeductedThread;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Component
public class TestTask {

    @Autowired
    private ClientMybatisPlusService clientService;


    /**
     * 模拟业务对用户额度调整操作
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void adjust() {
        LambdaQueryWrapper<ClientDO> salesmanQueryWrapper = new LambdaQueryWrapper<>();
        salesmanQueryWrapper.eq(ClientDO::getStatus, InfoStatusEnum.NORMAL.getCode());
        salesmanQueryWrapper.eq(ClientDO::getType, ClientTypeEnum.SALESMAN.getCode());
        List<ClientDO> salesmanList = clientService.list(salesmanQueryWrapper);
        if (!salesmanList.isEmpty()) {
            ClientDO salesman = salesmanList.get(0);
            LambdaQueryWrapper<ClientDO> clientQueryWrapper = new LambdaQueryWrapper<>();
            clientQueryWrapper.eq(ClientDO::getStatus, InfoStatusEnum.NORMAL.getCode());
            clientQueryWrapper.eq(ClientDO::getType, ClientTypeEnum.USER.getCode());
            List<ClientDO> list = clientService.list(clientQueryWrapper);
            if (!list.isEmpty()) {
                CountDownLatch countDownLatch = new CountDownLatch(100);
                for (ClientDO clientDO : list) {
                    Thread thread = new AdjustThread(countDownLatch, clientDO.getClientId(), salesman.getUsername());
                    thread.start();
                    countDownLatch.countDown();
                }
            }
        }
    }

    /**
     * 模拟用户额度操作
     */
    @Scheduled(cron = "0/31 * * * * ?")
    public void user() {
        LambdaQueryWrapper<ClientDO> clientQueryWrapper = new LambdaQueryWrapper<>();
        clientQueryWrapper.eq(ClientDO::getStatus, InfoStatusEnum.NORMAL.getCode());
        clientQueryWrapper.eq(ClientDO::getType, ClientTypeEnum.USER.getCode());
        List<ClientDO> list = clientService.list(clientQueryWrapper);
        //等待100个线程同时执行
        if (!list.isEmpty()) {
            CountDownLatch countDownLatch = new CountDownLatch(100);
            for (ClientDO clientDO : list) {
                Thread thread = new DeductedThread(countDownLatch, clientDO);
                thread.start();
                countDownLatch.countDown();
            }
        }

    }
}
