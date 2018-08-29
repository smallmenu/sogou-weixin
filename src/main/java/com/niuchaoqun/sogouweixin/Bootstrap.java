package com.niuchaoqun.sogouweixin;

import com.niuchaoqun.sogouweixin.config.SogouProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class Bootstrap implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    @Autowired
    private SogouProperties sogou;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Bootstrap Clear Temp File...");

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
