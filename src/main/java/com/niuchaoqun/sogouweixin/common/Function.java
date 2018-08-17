package com.niuchaoqun.sogouweixin.common;

import okhttp3.*;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class Function {
    public static void writeCookie(String fileName, List<Cookie> cookies) throws IOException {
        SerializableCookie serializableCookie = new SerializableCookie();

        File file = new File(fileName);
        ArrayList<String> lines = new ArrayList<>();

        for (Cookie cookie : cookies) {
            String c = serializableCookie.encode(cookie);
            lines.add(c);
        }
        FileUtils.writeLines(file, lines);
    }

    public static List<Cookie> readCookie(String fileName) throws IOException {
        SerializableCookie serializableCookie = new SerializableCookie();

        File file = new File(fileName);

        ArrayList<Cookie> cookies = new ArrayList<>();

        List<String> strings = FileUtils.readLines(file);
        for (int i = 0; i < strings.size(); i++) {
            Cookie d = serializableCookie.decode(strings.get(i));
            cookies.add(d);
        }
        return cookies;
    }

    public static void writeFile(String fileName, String data) throws IOException {
        File file = new File(fileName);
        FileUtils.writeStringToFile(file, data);
    }

    public static String readFile(String fileName) throws IOException {
        File file = new File(fileName);
        String data = FileUtils.readFileToString(file);
        return data;
    }
}
