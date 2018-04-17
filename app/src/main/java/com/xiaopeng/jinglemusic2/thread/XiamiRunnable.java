package com.xiaopeng.jinglemusic2.thread;

import android.os.Handler;
import android.os.Message;
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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

/**
 * Created by liujian on 2017/8/13.
 */

public class XiamiRunnable implements Runnable {
    private ArrayList<Music> songList;
    private Handler mHandler;
    private String songName;

    public XiamiRunnable() {
        super();
    }

    public XiamiRunnable(Handler mHandler, String songName) {
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
        Message msg = new Message();
        songList = new ArrayList<>();
        try {
            String requestUrl = "http://spark.api.xiami.com/sdk?v=sdk&search_content_type=0&method=search.all&search_source_type=1&key=" + songName + "&limit=5&show_invalid_song=1&device_id=77849f09-2929-395d-aa86-07fb2d35fb5b&app_v=2020000&api_key=249970aacbae1d776368af6c4726232d&call_id=1511271452580&api_sig=d67d3880821b59a0b16e3649a8497829";
            JSONObject jsonData = (new JSONObject(NetworkUtil.getJsonByGet(requestUrl))).getJSONObject("data");
            JSONArray jsonArray = jsonData.getJSONArray("songs");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonSongElement = jsonArray.getJSONObject(i);
                String musicId = jsonSongElement.getString("song_id");

                JSONObject jsonObj = new JSONObject(getJsonByGet("http://www.xiami.com/song/playlist/id/" + musicId + "/object_name/default/object_id/0/cat/json"));
                JSONObject jsonSong = jsonObj.getJSONObject("data").getJSONArray("trackList").getJSONObject(0);
                String songName = jsonSong.getString("songName") + "——" + jsonSong.getString("singers");
                String location = jsonSong.getString("location");
                String songLink = deCaesar(location);
                String songPic = jsonSong.getString("album_pic");
                String songLrc = jsonSong.getString("lyric");
                songList.add(new Music(songName, songLink, songPic, songLrc));

            }

            msg.what = 0;
            msg.obj = songList;
            mHandler.sendMessage(msg);
        } catch (IOException | JSONException e) {
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
