package com.niuchaoqun.sogouweixin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:sogou.properties")
@ConfigurationProperties(prefix = "sogou")
public class SogouProperties {
    private String seccodeUrl;

    private String pvUrl;

    private String seccodePostUrl;

    private String postString;

    private String cookieSeccodeFile;

    private String seccodeFile;

    private String suvFile;

    public String getSeccodeUrl() {
        return seccodeUrl;
    }

    public void setSeccodeUrl(String seccodeUrl) {
        this.seccodeUrl = seccodeUrl;
    }

    public String getPvUrl() {
        return pvUrl;
    }

    public void setPvUrl(String pvUrl) {
        this.pvUrl = pvUrl;
    }

    public String getSeccodePostUrl() {
        return seccodePostUrl;
    }

    public void setSeccodePostUrl(String seccodePostUrl) {
        this.seccodePostUrl = seccodePostUrl;
    }

    public String getPostString() {
        return postString;
    }

    public void setPostString(String postString) {
        this.postString = postString;
    }

    public String getCookieSeccodeFile() {
        return cookieSeccodeFile;
    }

    public void setCookieSeccodeFile(String cookieSeccodeFile) {
        this.cookieSeccodeFile = cookieSeccodeFile;
    }

    public String getSeccodeFile() {
        return seccodeFile;
    }

    public void setSeccodeFile(String seccodeFile) {
        this.seccodeFile = seccodeFile;
    }

    public String getSuvFile() {
        return suvFile;
    }

    public void setSuvFile(String suvFile) {
        this.suvFile = suvFile;
    }

    @Override
    public String toString() {
        return "SogouProperties{" +
                "seccodeUrl='" + seccodeUrl + '\'' +
                ", pvUrl='" + pvUrl + '\'' +
                ", seccodePostUrl='" + seccodePostUrl + '\'' +
                ", postString='" + postString + '\'' +
                ", cookieSeccodeFile='" + cookieSeccodeFile + '\'' +
                ", seccodeFile='" + seccodeFile + '\'' +
                ", suvFile='" + suvFile + '\'' +
                '}';
    }
}
