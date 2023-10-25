package com.accountquota.thread;

import com.accountquota.mybatisplus.entity.ClientDO;
import com.accountquota.util.HttpUtil;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class DeductedThread extends Thread {

    private CountDownLatch countDownLatch;
    private ClientDO client;

    public DeductedThread(CountDownLatch countDownLatch, ClientDO client) {
        this.countDownLatch = countDownLatch;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            String url = "http://localhost:8100/accountquota/quota/user/deducted";
            Map<String, String> header = new HashMap<>();
            header.put("username", client.getUsername());
            Map<String, String> param = new HashMap<>();
            param.put("type", String.valueOf(new Random().nextInt(10)));
            param.put("quotaValue", BigDecimal.valueOf(getRandomQuota()).toString());
            String s = HttpUtil.doPostJson(url, header, JSONObject.toJSONString(param));
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private double getRandomQuota() {
        double random = new Random().nextDouble();
        double sp = 0.0 + (random * (1000000.0 - 0.0));
        return Double.parseDouble(String.format("%.6f", sp));
    }
}
