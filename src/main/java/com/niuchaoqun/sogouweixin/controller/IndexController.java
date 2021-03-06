package com.niuchaoqun.sogouweixin.controller;

import com.niuchaoqun.sogouweixin.common.BaseController;
import com.niuchaoqun.sogouweixin.common.Response;
import com.niuchaoqun.sogouweixin.config.SogouProperties;
import com.niuchaoqun.sogouweixin.pojo.TakePojo;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * 请求接口
 */
@RestController
public class IndexController extends BaseController {

    @Autowired
    private SogouProperties sogou;

    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;

    @RequestMapping("/")
    @ResponseBody
    public String index() {
        return "Hello World";
    }

    @RequestMapping("/stat")
    public Object stat() {
        HashMap<String, Long> data = new HashMap<>();

        if (checkRunning()) {
            data.put("running", 1L);
        } else {
            data.put("running", 0L);
        }

        ListOperations<String, String> list = stringRedisTemplate.opsForList();
        Long total = list.size(sogou.getRedisKey());

        data.put("total", total);

        return Response.data(data);
    }

    @RequestMapping("/take")
    public Object take() throws IOException {
        if (checkRunning()) {
            ListOperations<String, String> list = stringRedisTemplate.opsForList();

            TakePojo takePojo = new TakePojo();
            String snuid = list.leftPop(sogou.getRedisKey());

            if (snuid != null) {
                takePojo.setSnuid(snuid);

                Long total = list.size(sogou.getRedisKey());
                takePojo.setTotal(total);

                String suv = FileUtils.readFileToString(new File(sogou.getSuvFile()));
                takePojo.setSuv(suv);
                return Response.data(takePojo);
            }
        }

        return Response.error("not running or empty");
    }

    private boolean checkRunning() {
        File seccodeFile = new File(sogou.getSeccodeFile());
        File cookieSeccodeFile = new File(sogou.getCookieSeccodeFile());
        File suv = new File(sogou.getSuvFile());
        if (seccodeFile.exists() && cookieSeccodeFile.exists() && suv.exists()) {
            return true;
        }
        return false;
    }
}
