package com.xiaopeng.jinglemusic2.thread;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.xiaopeng.jinglemusic2.Music;
import com.xiaopeng.jinglemusic2.utils.NetworkUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by liujian on 2017/8/29.
 */

public class MiguRunnable implements Runnable {
    private ArrayList<Music> songList;
    private Handler mHandler;
    private String songName;

    public MiguRunnable() {
        super();
    }

    public MiguRunnable(Handler mHandler, String songName) {
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
            String requestUrl = "http://c.musicapp.migu.cn/MIGUM2.0/v1.0/content/search_suggest.do?&ua=Android_migu&version=5.0.7&text=" + songName + "&type=0";
            JSONObject jsonObject = new JSONObject(NetworkUtil.getJsonByGet(requestUrl));
            JSONArray jsonArray = jsonObject.getJSONArray("songSuggests");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectInner = jsonArray.getJSONObject(i);
                String songTitle = jsonObjectInner.getString("name") + "——" + jsonObjectInner.getString("singerName");
                String songId = jsonObjectInner.getString("id");
                String requetUrlAnother = "http://music.migu.cn/webfront/player/findsong.do?itemid=" + songId + "&type=song";
                JSONObject jsonObjectAnother = new JSONObject(NetworkUtil.getJsonByGet(requetUrlAnother));
                JSONArray jsonArrayAnother = jsonObjectAnother.getJSONArray("msg");
                JSONObject jsonMusic = jsonArrayAnother.getJSONObject(0);
                String songPic = jsonObjectInner.getJSONArray("imgItems").getJSONObject(1).getString("img");
                String songLink;
                if (!TextUtils.isEmpty(jsonMusic.getString("hdmp3"))) {
                    songLink = jsonMusic.getString("hdmp3");
                } else {
                    songLink = jsonMusic.getString("mp3");
                }
                String songLrc = null;
                songList.add(new Music(songTitle, songLink, songPic, songLrc));

            }

            msg.what = 0;
            msg.obj = songList;
            mHandler.sendMessage(msg);

        } catch (JSONException e) {
            Log.e("liujian", e.toString());
            msg.what = 1;
            mHandler.sendMessage(msg);
        } catch (IOException e) {
            Log.e("liujian", e.toString());
            msg.what = 1;
            mHandler.sendMessage(msg);
        }

    }


    public String getJsonByGet(String address) {
        String response = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(address);
            httpGet.setHeader("User-Agent", "User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.104 Safari/537.36 Core/1.53.3103.400 QQBrowser/9.6.11372.400");
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
