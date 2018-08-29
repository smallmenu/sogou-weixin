package com.niuchaoqun.sogouweixin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SogouWeixinApplication {

    public static void main(String[] args) {
        SpringApplication.run(SogouWeixinApplication.class, args);
    }
}
