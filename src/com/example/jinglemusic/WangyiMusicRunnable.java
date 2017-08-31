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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by liujian on 2017/8/11.
 */

public class WangyiMusicRunnable implements Runnable {
    private ArrayList<Music> songList;
    private Handler mHandler;
    private String songName;
    public WangyiMusicRunnable() {
        super();
    }

    public WangyiMusicRunnable(Handler mHandler,String songName){
        this.mHandler = mHandler;
        this.songName =songName;
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
        Message msg = new Message();
        songList = new ArrayList<>();
        try {
            String params = "s="+ URLEncoder.encode(songName,"UTF-8") +"&offset=20"+"&limit=20"+"&type=1";
            String urlBase = "http://music.163.com/api/search/pc";
            try {
                JSONObject jsonObject = new JSONObject(getJsonByPost(urlBase,params));
                String statuCode = jsonObject.getString("code");
                if (!"200".equals(statuCode)){
                    return;
                }
                JSONObject jsonObject2 = jsonObject.getJSONObject("result");
                JSONArray jsonArray = jsonObject2.getJSONArray("songs");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                   // String songName = json.getString("name")+json.getJSONArray("artists");
                    String songId = json.getString("id");
                    JSONObject jsonById = new JSONObject(getJsonByGet("http://music.163.com/api/song/detail/?id="+songId+"&ids=%5B"+songId+"%5D"));
                    JSONObject json_song = jsonById.getJSONArray("songs").getJSONObject(0);
                    String songName = json_song.getString("name");
                    String artist = json_song.getJSONArray("artists").getJSONObject(0).getString("name");
                    songName = songName +"——"+ artist;
                    String songLink = json_song.getString("mp3Url");
                    String songPic = json_song.getJSONObject("album").getString("blurPicUrl");
                   // JSONObject jsonLrc = new JSONObject(getJsonByGet("http://music.163.com/api/song/lyric?os=pc&id="+songId+"&lv=-1&kv=-1&tv=-1"));
                    String songLrc = null;//json歌词数据
                    if (songLink.equals("null")){
                        songLink ="fuck163.com";
                    }
                    songList.add(new Music(songName, songLink,songPic,songLrc));

                }
                msg.what = 0;
                msg.obj = songList;
                mHandler.sendMessage(msg);
                
            } catch (JSONException e) {
                msg.what =1;
                mHandler.sendMessage(msg);
            }
        } catch (UnsupportedEncodingException e) {
            msg.what =1;
            mHandler.sendMessage(msg);
        }

    }


    private String getJsonByPost(String urlString, String params){
        StringBuilder stringBuilder = new StringBuilder();
        Message msg = new Message();
        DataOutputStream dataOutputStream = null;
        HttpURLConnection connection = null;
        try {

            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Cookie","appver=1.5.0.75771");
            connection.setRequestProperty("Referer","http://music.163.com/");
            connection.connect();
            dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(params);
            dataOutputStream.flush();
            dataOutputStream.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while((line = reader.readLine()) != null ){
                stringBuilder.append(line);
            }

        } catch (IOException e) {
            msg.what =1;
            mHandler.sendMessage(msg);
        }finally {
            if (connection != null){
                connection.disconnect();
            }
            if (dataOutputStream != null){
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    msg.what =1;
                    mHandler.sendMessage(msg);
                }
            }
            return String.valueOf(stringBuilder);
        }

    }

    public String getJsonByGet(String address) {
        String response = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(address);
            httpGet.setHeader("Cookie","appver=1.5.0.75771");
            httpGet.setHeader("Referer","http://music.163.com/");
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
