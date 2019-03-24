package com.xiaopeng.jinglemusic2.thread;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.xiaopeng.jinglemusic2.Music;
import com.xiaopeng.jinglemusic2.bean.MusicInfo;
import com.xiaopeng.jinglemusic2.model.search.SearchModel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author liujian
 * @date 2017/8/12
 */

public class BaiduMp3Runnable implements Runnable {
    private String mSongName;
    private SearchModel.LoadCallback mLoadCallback;

    public BaiduMp3Runnable(String songName, SearchModel.LoadCallback mLoadCallback) {
        this.mSongName = songName;
        this.mLoadCallback = mLoadCallback;

    }


    @Override
    public void run() {
        ArrayList<Music> songList = new ArrayList<>();
        try {
            Gson gson = new Gson();

            String requestUrl = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=android&version=5.6.5.0&method=baidu.ting.search.catalogSug&format=json&query=" + URLEncoder.encode(mSongName, "UTF-8");
            List<MusicInfo> baiduMpsInfos = gson.fromJson(getJsonByGet(requestUrl), new TypeToken<List<MusicInfo>>() {
            }.getType());
            for (MusicInfo info : baiduMpsInfos) {

                String songId = info.getSongid();
                JsonObject jsonObject = new JsonParser().parse(getJsonByGet("http://music.baidu.com/data/music/links?songIds=" + songId)).getAsJsonObject();

                JsonArray jsonArray = jsonObject.getAsJsonObject("data").getAsJsonArray("songList");

                Music music = gson.fromJson(jsonArray.get(0), Music.class);
                songList.add(music);

            }

            if (mLoadCallback != null){
                mLoadCallback.onSuccess(songList);
            }



        } catch (Exception e) {
            if (mLoadCallback != null){
                mLoadCallback.onFailed(e);
            }
        }
    }

    /**
     * 百度mp3好恶心，okhttp请求的response时403错误
     * 所以只能继续使用httpClient了
     *
     * @param address 請求網址
     * @return json数据
     */
    public String getJsonByGet(String address) {
        String response = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(address);
            httpGet.setHeader("User-Agent", "User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.104 Safari/537.36 Core/1.53.3103.400 QQBrowser/9.6.11372.400");
            httpGet.setHeader("Host", "music.baidu.com");
            httpGet.setHeader("Upgrade-Insecure-Requests", "1");
            httpGet.setHeader("Cookie", "BIDUPSID=8B63C1554C011084DD76876BB928394B; PSTM=1494468558; BAIDUID=C931599147CFEEF99E8E3644CF922C30:FG=1; BDUSS=053Y0UyaVlTN2tCZGFHVTdtRG5TWm1TWH5kanZnVUlPMDE4dFk2N0J3NmZwVUJaSVFBQUFBJCQAAAAAAAAAAAEAAAC9sNcKNjk1OTY2MDA0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAJ8YGVmfGBlZZ; __cfduid=d5bda503e2d339b6b38809ec6d3941c521495517645; MCITY=-%3A; u_lo=0; u_id=; u_t=; Hm_lvt_d0ad46e4afeacf34cd12de4c9b553aa6=1501570196; BDRCVFR[S_ukKV6dOkf]=mk3SLVN4HKm; PSINO=2; H_PS_PSSID=1440_21087_20718");
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
}
