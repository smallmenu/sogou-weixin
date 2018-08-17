package com.niuchaoqun.sogouweixin.controller;

import com.niuchaoqun.sogouweixin.common.Response;
import com.niuchaoqun.sogouweixin.config.SogouProperties;
import com.niuchaoqun.sogouweixin.repository.SnuidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.HashMap;

@RestController
public class IndexController {

    @Autowired
    private SnuidRepository snuidRepository;

    @Autowired
    private SogouProperties sogou;

    @RequestMapping("/")
    public Object index() {
        HashMap<String, Long> data = new HashMap<String, Long>();


        File seccodeFile = new File(sogou.getSeccodeFile());
        File cookieSeccodeFile = new File(sogou.getCookieSeccodeFile());
        if (seccodeFile.exists() && cookieSeccodeFile.exists()) {
            data.put("running", 1L);
        } else {
            data.put("running", 0L);
        }

        long total = snuidRepository.count();
        data.put("total", total);

        return Response.data(data);
    }
}
