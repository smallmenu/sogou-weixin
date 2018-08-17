/**
 *
 */
package com.niuchaoqun.sogouweixin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niuchaoqun.sogouweixin.config.SogouProperties;
import com.niuchaoqun.sogouweixin.common.CookieJarImp;
import com.niuchaoqun.sogouweixin.common.BaseController;
import com.niuchaoqun.sogouweixin.pojo.SogouMsg;
import com.niuchaoqun.sogouweixin.common.Function;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/sogou")
public class SogouController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(SogouController.class);

    @Autowired
    private SogouProperties sogou;

    @RequestMapping("")
    public String index() {
        File cookieSeccode = new File(sogou.getCookieSeccodeFile());
        File seccode = new File(sogou.getSeccodeFile());
        if (cookieSeccode.exists()) {
            cookieSeccode.delete();
        }
        if (seccode.exists()) {
            seccode.delete();
        }

        return "sogou/index";
    }

    @RequestMapping(value = "/submit", method = {RequestMethod.POST})
    @ResponseBody
    public String submit(@RequestParam("seccode") String seccode) throws IOException {
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
                .cookieJar(cookieJarImp)
                .build();
        Response response = seccodeClient.newCall(
                new Request.Builder()
                        .url(sogou.getSeccodePostUrl())
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
                Function.writeFile(sogou.getSeccodeFile(), seccode);
                return json;
            } else {
                logger.info(sogouMsg.getMsg());
                return sogouMsg.getMsg();
            }
        } else {
            return "验证码提交错误";
        }
    }

    @RequestMapping(value = "/seccode", produces = {MediaType.IMAGE_JPEG_VALUE})
    @ResponseBody
    public byte[] secode() throws IOException {
        long tc = System.currentTimeMillis();
        String seccode_url = sogou.getSeccodeUrl() + tc;
        String pv_url = sogou.getPvUrl() + (tc + 23);

        List<Cookie> cookies;
        Response response;
        String suv = null;

        // PV请求
        CookieJarImp cookiePv = new CookieJarImp();
        OkHttpClient pvClient = new OkHttpClient()
                .newBuilder()
                .cookieJar(cookiePv)
                .build();
        response = pvClient.newCall(
                new Request.Builder().url(pv_url).build()
        ).execute();
        cookies = cookiePv.getCookies();
        logger.info(cookies.toString());

        for (Cookie cookie : cookies) {
            if ("SUV".equals(cookie.name())) {
                suv = cookie.value();
            }
        }
        if (suv != null) {
            Function.writeFile(sogou.getSuvFile(), suv);

            // 验证码请求
            CookieJarImp cookieSeccode = new CookieJarImp();
            OkHttpClient seccodeClient = new OkHttpClient()
                    .newBuilder()
                    .cookieJar(cookieSeccode)
                    .build();
            response = seccodeClient.newCall(
                    new Request.Builder().url(seccode_url).build()
            ).execute();

            if (response.isSuccessful() && response.body() != null) {
                byte[] image = response.body().bytes();

                cookies = cookieSeccode.getCookies();
                logger.info(cookies.toString());

                Function.writeCookie(sogou.getCookieSeccodeFile(), cookies);

                return image;
            }
        }

        return null;
    }
}
