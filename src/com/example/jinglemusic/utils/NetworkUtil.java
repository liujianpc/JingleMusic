package com.example.jinglemusic.utils;

import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Date: 2017/11/22
 * Created by XP-PC-XXX
 */

public class NetworkUtil {
    public static final OkHttpClient okHttpClient = new OkHttpClient();

    public static String getJsonByGet(String url) throws IOException {
        String result = null;
        Request request = new Request.Builder()
                .addHeader("User-Agent", "User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.104 Safari/537.36 Core/1.53.3103.400 QQBrowser/9.6.11372.400")
                .url(url).cacheControl(new CacheControl.Builder().maxAge(3600, TimeUnit.SECONDS).build()).build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            result = response.body().string();
        }
        return result;
    }

    public static String xiaMigetJsonByGetWithHeader(String url) throws IOException {
        String result = null;
        Request request = new Request.Builder()
                .addHeader("User-Agent", "User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.104 Safari/537.36 Core/1.53.3103.400 QQBrowser/9.6.11372.400")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .addHeader("Cache-Control", "0")
                .addHeader("Connection", "keep-alive")
                .addHeader("Cookie", "_unsign_token=5b7b927a8ad5dd072ff9b8bee5d56704; _xiamitoken=d128dee2bbfd07f7ced6b309a0984caa; gid=151133186784545")
                .addHeader("Host", "www.xiami.com")
                .addHeader("Upgrade-Insecure-Requests", "1")
                .url(url).cacheControl(new CacheControl.Builder().maxAge(3600, TimeUnit.SECONDS).build()).build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            result = response.body().string();
        }
        return result;
    }

    public static String getJsonByPost(String url, HashMap<String, String> map) throws IOException {
        String result = null;
        FormEncodingBuilder requestBuillder = new FormEncodingBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()
                ) {
            requestBuillder.add(entry.getKey(), entry.getValue());
        }
        RequestBody requestBody = requestBuillder.build();
        Request request = new Request.Builder().url(url).post(requestBody).cacheControl(new CacheControl.Builder().maxAge(3600, TimeUnit.SECONDS).build()).build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            result = response.body().string();
        }
        return result;

    }

    public static String getJsonByGetWithHeader(String url) throws IOException {
        String result = null;
        Request request = new Request.Builder().url(url)
                .header("Cookie", "appver=1.5.0.75771")
                .addHeader("Referer", "http://music.163.com/")
                .cacheControl(new CacheControl.Builder().maxAge(3600, TimeUnit.SECONDS).build())
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            result = response.body().string();
        }
        return result;

    }

}
