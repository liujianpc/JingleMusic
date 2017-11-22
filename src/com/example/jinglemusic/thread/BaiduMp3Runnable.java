package com.example.jinglemusic.thread;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.jinglemusic.model.Music;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by liujian on 2017/8/12.
 */

public class BaiduMp3Runnable implements Runnable {
    private ArrayList<Music> songList;
    private Handler mHandler;
    private String songName;

    public BaiduMp3Runnable() {
        super();
    }

    public BaiduMp3Runnable(Handler mHandler, String songName) {
        this.mHandler = mHandler;
        this.songName = songName;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        songList = new ArrayList<>();
        Message msg = new Message();
        try {
            String requestUrl = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=android&version=5.6.5.0&method=baidu.ting.search.catalogSug&format=json&query=" + URLEncoder.encode(songName, "UTF-8");
            JSONObject jsonObject = new JSONObject(getJsonByGet(requestUrl));
            JSONArray jsonArray = jsonObject.getJSONArray("song");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonInner = jsonArray.getJSONObject(i);
                String songName = jsonInner.getString("songname");
                String songId = jsonInner.getString("songid");
                String artist = jsonInner.getString("artistname");
                songName = songName + "——" + artist;
                JSONObject jsonAnother = new JSONObject(getJsonByGet("http://music.baidu.com/data/music/links?songIds=" + songId));
                JSONObject jsonData = jsonAnother.getJSONObject("data");
                JSONArray jsonSongs = jsonData.getJSONArray("songList");
                JSONObject jsonSongChild = jsonSongs.getJSONObject(0);
                String songLink = jsonSongChild.getString("songLink");
                String songPic = jsonSongChild.getString("songPicBig");
                String songLrc = jsonSongChild.getString("lrcLink");

                songList.add(new Music(songName, songLink, songPic, songLrc));

            }

            msg.what = 0;
            msg.obj = songList;
            mHandler.sendMessage(msg);


        } catch (UnsupportedEncodingException | JSONException e) {
            Log.e("liujian", e.toString());
            msg.what = 1;
            mHandler.sendMessage(msg);
        }
    }

    /**
     * 百度mp3好恶心，okhttp请求的response时403错误
     * 所以只能继续使用httpClient了
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
