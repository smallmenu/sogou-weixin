package com.niuchaoqun.sogouweixin.controller;

import com.niuchaoqun.sogouweixin.common.Response;
import com.niuchaoqun.sogouweixin.config.SogouProperties;
import com.niuchaoqun.sogouweixin.entity.Snuid;
import com.niuchaoqun.sogouweixin.pojo.TakePojo;
import com.niuchaoqun.sogouweixin.repository.SnuidRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class IndexController {

    @Autowired
    private SnuidRepository snuidRepository;

    @Autowired
    private SogouProperties sogou;

    @RequestMapping("/")
    @ResponseBody
    public String index() {
        return "Hello World";
    }

    @RequestMapping("/stat")
    public Object stat() {
        HashMap<String, Long> data = new HashMap<String, Long>();

        if (checkRunning()) {
            data.put("running", 1L);
        } else {
            data.put("running", 0L);
        }

        long total = snuidRepository.count();
        data.put("total", total);

        return Response.data(data);
    }

    @RequestMapping("/take")
    public Object take() throws IOException {

        if (checkRunning()) {
            TakePojo takePojo = new TakePojo();
            long total = snuidRepository.count();

            if (total > 0) {
                String suv = FileUtils.readFileToString(new File(sogou.getSuvFile()));
                takePojo.setSuv(suv);
                takePojo.setTotal(total);
                Snuid take = snuidRepository.findFirstByOrderByCreatedAsc();

                if (take != null) {
                    takePojo.setSnuid(take.getSnuid());
                    snuidRepository.delete(take);

                    return Response.data(takePojo);
                }
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
