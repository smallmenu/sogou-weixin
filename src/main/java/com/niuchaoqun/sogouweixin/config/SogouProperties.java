package com.niuchaoqun.sogouweixin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:sogou.properties")
@ConfigurationProperties(prefix = "sogou")
@Data
public class SogouProperties {
    private String seccodeUrl;

    private String pvUrl;

    private String seccodePostUrl;

    private String postString;

    private String cookieSeccodeFile;

    private String seccodeFile;

    private String suvFile;

    private String userAgent;

    private String redisKey;
}
