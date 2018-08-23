package com.niuchaoqun.sogouweixin.cron;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niuchaoqun.sogouweixin.config.SogouProperties;
import com.niuchaoqun.sogouweixin.common.CookieJarImp;
import com.niuchaoqun.sogouweixin.entity.Snuid;
import com.niuchaoqun.sogouweixin.pojo.SogouMsg;
import com.niuchaoqun.sogouweixin.common.Function;
import com.niuchaoqun.sogouweixin.repository.SnuidRepository;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 根据模拟提交的验证码Cookie，循环生成 SNUID
 */
@Component
public class SogouPost {
    private static final Logger logger = LoggerFactory.getLogger(SogouPost.class);

    @Autowired
    private SogouProperties sogou;

    @Autowired
    private SnuidRepository snuidRepository;

    @Scheduled(fixedDelay = 200)
    public void generatorSnuid() throws IOException {
        memory();

        File seccodeFile = new File(sogou.getSeccodeFile());
        File cookieSeccodeFile = new File(sogou.getCookieSeccodeFile());

        if (seccodeFile.exists() && cookieSeccodeFile.exists()) {
            String seccode = Function.readFile(sogou.getSeccodeFile());

            RequestBody posts = new FormBody.Builder()
                    .add("c", seccode)
                    .add("r", sogou.getPostString())
                    .add("v", "5")
                    .build();

            List<Cookie> cookies = Function.readCookie(sogou.getCookieSeccodeFile());
            CookieJarImp cookieJarImp = new CookieJarImp();
            cookieJarImp.setCookies(cookies);

            OkHttpClient seccodeClient = new OkHttpClient()
                    .newBuilder()
                    .connectTimeout(1500, TimeUnit.MILLISECONDS)
                    .cookieJar(cookieJarImp)
                    .build();
            Response response = seccodeClient.newCall(
                    new Request.Builder()
                            .url(sogou.getSeccodePostUrl())
                            .header("User-Agent", sogou.getUserAgent())
                            .post(posts)
                            .build()
            ).execute();

            // 模拟提交验证码
            if (response.isSuccessful()) {
                String json = response.body().string();

                logger.info(json);

                ObjectMapper mapper = new ObjectMapper();
                SogouMsg sogouMsg = mapper.readValue(json, SogouMsg.class);

                if (sogouMsg.getCode() == 0) {
                    String idString = sogouMsg.getId();
                    if (idString.length() == 32) {
                        Snuid snuid = new Snuid();
                        snuid.setSnuid(idString);
                        Snuid insert = snuidRepository.save(snuid);
                    }
                } else {
                    logger.info(sogouMsg.getMsg());
                }
            } else {
                logger.info("Error");
            }
        } else {
            logger.info("Missing Seccode File...");
        }
    }

    private void memory() {
        Runtime runtime = Runtime.getRuntime();

        long free = runtime.freeMemory() / 1024 / 1024;
        long total = runtime.totalMemory() / 1024 / 1024;
        long max = runtime.maxMemory() / 1024 / 1024;

        logger.info("Memory: " +free + "M/" + total + "M/" + max + "M");
    }
}
