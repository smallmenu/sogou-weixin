package com.niuchaoqun.sogouweixin;

import com.niuchaoqun.sogouweixin.config.SogouProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;


@SpringBootApplication
@EnableScheduling
public class SogouWeixinApplication {
    private static final Logger logger = LoggerFactory.getLogger(SogouWeixinApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SogouWeixinApplication.class, args);
    }

    @Autowired
    public SogouWeixinApplication(SogouProperties sogou) {
        logger.info("Clear Temp File...");
        logger.info(sogou.toString());
        File cookieSeccode = new File(sogou.getCookieSeccodeFile());
        File seccode = new File(sogou.getSeccodeFile());
        File suv = new File(sogou.getSuvFile());
        if (cookieSeccode.exists()) {
            cookieSeccode.delete();
        }
        if (seccode.exists()) {
            seccode.delete();
        }
        if (suv.exists()) {
            suv.delete();
        }
    }

}
