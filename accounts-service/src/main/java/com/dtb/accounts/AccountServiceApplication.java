package com.dtb.accounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
@SpringBootApplication
//@SpringBootConfiguration
@EnableFeignClients(basePackages = "com.dtb.accounts.feigns")
@ImportAutoConfiguration({FeignAutoConfiguration.class})
public class AccountServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);

    }

}