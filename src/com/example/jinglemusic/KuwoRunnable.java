package com.example.jinglemusic;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by liujian on 2017/8/28.
 */

public class KuwoRunnable implements Runnable {

    private ArrayList<Music> songList;
    private Handler mHandler;
    private String songName;

    public KuwoRunnable(){super();}
    public KuwoRunnable(Handler mHandler, String songName){
        this.mHandler =mHandler;
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
        try{
            String requestUrl = "http://search.kuwo.cn/r.s?all="+songName+"&ft=music&itemset=web_2013&client=kt&pn=0&rn=5&rformat=json&encoding=utf8";
            JSONObject jsonObject = new JSONObject(getJsonByGet(requestUrl));
            JSONArray jsonArray = jsonObject.getJSONArray("abslist");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectInner = jsonArray.getJSONObject(i);
                String songTitle = jsonObjectInner.getString("SONGNAME")+"——"+jsonObjectInner.getString("ARTIST");
                String songId = jsonObjectInner.getString("MUSICRID");
                String requetUrlAnother = "http://antiserver.kuwo.cn/anti.s?type=convert_url&rid="+songId+"&format=mp3&response=url";
                String songLink = getJsonByGet(requetUrlAnother);
                String songPic = null;
                String songLrc = null;
                songList.add(new Music(songTitle,songLink,songPic,songLrc));

            }

            msg.what = 0;
            msg.obj = songList;
            mHandler.sendMessage(msg);

        } catch (JSONException e) {
            Log.e("liujian",e.toString());
            msg.what = 1;
            mHandler.sendMessage(msg);
        }

    }


    public String getJsonByGet(String address) {
        String response = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(address);
            httpGet.setHeader("User-Agent","User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.104 Safari/537.36 Core/1.53.3103.400 QQBrowser/9.6.11372.400");
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
