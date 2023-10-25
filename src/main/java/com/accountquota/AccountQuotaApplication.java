package com.accountquota;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.accountquota"})
@MapperScan("com.accountquota.mybatisplus.mapper")
public class AccountQuotaApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountQuotaApplication.class, args);
    }
}
