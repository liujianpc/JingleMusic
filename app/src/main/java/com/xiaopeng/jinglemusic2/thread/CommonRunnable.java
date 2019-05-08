package com.xiaopeng.jinglemusic2.thread;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.xiaopeng.jinglemusic2.Music;
import com.xiaopeng.jinglemusic2.model.search.SearchModel;
import com.xiaopeng.jinglemusic2.utils.NetworkUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author liujian
 * @date 2017/8/13
 */

public class CommonRunnable implements Runnable {
    private static final int OK_CODE = 200;
    private String mSongName;
    private String mType;
    private SearchModel.LoadCallback mLoadCallback;


    public CommonRunnable(String songName, String type, SearchModel.LoadCallback callback) {
        this.mSongName = songName;
        this.mType = type;
        this.mLoadCallback = callback;
    }


    @Override
    public void run() {
        ArrayList<Music> songList;
        try {
            String requestUrl = "http://music.sonimei.cn/";//"http://music.ifkdy.com/";
            HashMap<String, String> paramsMap = new HashMap<>(4);
            paramsMap.put("input", mSongName);
            paramsMap.put("filter", "name");
            paramsMap.put("type", mType);
            paramsMap.put("page", "1");

            Gson gson = new Gson();

            JsonObject jsonObject = new JsonParser().parse(NetworkUtil.getJsonByPost(requestUrl, paramsMap)).getAsJsonObject();
            int code = jsonObject.get("code").getAsInt();
            if (code == OK_CODE) {
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                songList = gson.fromJson(jsonArray, new TypeToken<List<Music>>() {
                }.getType());


                if (mLoadCallback != null) {
                    mLoadCallback.onSuccess(songList);
                }
            } else {
                if (mLoadCallback != null) {
                    String errorMsg = jsonObject.get("error").getAsString();
                    mLoadCallback.onFailed(new IllegalStateException(errorMsg));
                }

            }

        } catch (Exception e) {
            if (mLoadCallback != null) {
                mLoadCallback.onFailed(e);
            }
        }
    }


    public String getJsonByGet(String address) {
        String response = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(address);
            httpGet.setHeader("User-Agent", "User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.104 Safari/537.36 Core/1.53.3103.400 QQBrowser/9.6.11372.400");
//            httpGet.setHeader("Host","music.baidu.com");
//            httpGet.setHeader("Upgrade-Insecure-Requests","1");
//            httpGet.setHeader("Cookie","BIDUPSID=8B63C1554C011084DD76876BB928394B; PSTM=1494468558; BAIDUID=C931599147CFEEF99E8E3644CF922C30:FG=1; BDUSS=053Y0UyaVlTN2tCZGFHVTdtRG5TWm1TWH5kanZnVUlPMDE4dFk2N0J3NmZwVUJaSVFBQUFBJCQAAAAAAAAAAAEAAAC9sNcKNjk1OTY2MDA0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAJ8YGVmfGBlZZ; __cfduid=d5bda503e2d339b6b38809ec6d3941c521495517645; MCITY=-%3A; u_lo=0; u_id=; u_t=; Hm_lvt_d0ad46e4afeacf34cd12de4c9b553aa6=1501570196; BDRCVFR[S_ukKV6dOkf]=mk3SLVN4HKm; PSINO=2; H_PS_PSSID=1440_21087_20718");
            HttpResponse httpResponse = client.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = httpResponse.getEntity();
                response = EntityUtils.toString(entity, "utf-8");
            }

        } catch (Exception e) {
            // TODO: handle exception
            Log.e("exception", "json解析错误");
        }
        return response;
    }

    public String deCaesar(String location) {
        char rows = location.charAt(0); //获取行数
        int baseLine = ((location.length() - 1) / Integer.valueOf(rows + "")); //获取每行字符数
        int mode = (location.length() - 1) % Integer.valueOf(rows + ""); //获取余数
        String newString = ""; //准备拼凑字符串
        for (int i = 1; i <= baseLine + (mode > 0 ? 1 : 0); i++) {
            for (int r = 1; r <= Integer.valueOf(rows + ""); r++) {
                if (r <= mode) {
                    // if (((r - 1) * (baseLine + 1) + i) == location.length()) break;
                    newString += String.valueOf(location.charAt(((r - 1) * (baseLine + 1) + i)));
                    // newString += location.substring(((r - 1) * (baseLine + 1) + i), ((r - 1) * (baseLine + 1) + i)+1);
                } else {
                    if ((mode * (baseLine + 1) + (r - mode - 1) * baseLine + i) == location.length()) {
                        newString += String.valueOf(location.charAt((mode * (baseLine + 1) + (r - mode - 1) * baseLine + i) - 1));
                    } else {
                        newString += String.valueOf(location.charAt((mode * (baseLine + 1) + (r - mode - 1) * baseLine + i)));
                    }
                    //newString += location.substring((mode * (baseLine + 1) + (r - mode - 1) * baseLine + i), (mode * (baseLine + 1) + (r - mode - 1) * baseLine + i)+1);
                }
            }
        }
        String stringReady = "";
        try {
            stringReady = URLDecoder.decode(newString.substring(0, newString.lastIndexOf("null") + 4), "UTF-8").replace("^", "0");
            Log.e("liujian", stringReady);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return stringReady;
    }
}
