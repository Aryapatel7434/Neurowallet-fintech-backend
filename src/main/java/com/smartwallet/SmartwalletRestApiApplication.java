package com.smartwallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication
@EnableScheduling
public class SmartwalletRestApiApplication {

    public static void main(String[] args) {

        SpringApplication.run(
                SmartwalletRestApiApplication.class,
                args
        );
    }
}