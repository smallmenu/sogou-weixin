package com.niuchaoqun.sogouweixin.common;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import java.util.ArrayList;
import java.util.List;

public class CookieJarImp implements CookieJar {
    private List<Cookie> cookies;

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        this.cookies =  cookies;
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        if (cookies != null)
            return cookies;
        return new ArrayList<Cookie>();
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }
}
