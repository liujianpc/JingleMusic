package com.xiaopeng.jinglemusic2.thread;

import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.xiaopeng.jinglemusic2.Music;
import com.xiaopeng.jinglemusic2.bean.MusicInfo;
import com.xiaopeng.jinglemusic2.utils.NetworkUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujian on 2017/8/28.
 */

public class KuwoRunnable implements Runnable {

    private ArrayList<Music> songList;
    private Handler mHandler;
    private String songName;

    public KuwoRunnable() {
        super();
    }

    public KuwoRunnable(Handler mHandler, String songName) {
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
            Gson gson = new Gson();
            String requestUrl = "http://search.kuwo.cn/r.s?all=" + songName + "&ft=music&itemset=web_2013&client=kt&pn=0&rn=5&rformat=json&encoding=utf8";
            JsonObject jsonObject = new JsonParser().parse(NetworkUtil.getJsonByGet(requestUrl)).getAsJsonObject();
            JsonArray jsonArray = jsonObject.getAsJsonArray("abslist");
            List<MusicInfo> MusicInfos = gson.fromJson(jsonArray, new TypeToken<List<MusicInfo>>() {
            }.getType());
            for (MusicInfo info : MusicInfos) {
                String singerName = info.getArtistname();
                String songTitle = info.getSongname() + "——" + singerName;
                String songId = info.getSongid();
                String requetUrlAnother = "http://antiserver.kuwo.cn/anti.s?type=convert_url&rid=" + songId + "&format=mp3&response=url";
                String songLink = NetworkUtil.getJsonByGet(requetUrlAnother);
                String songPic = "";
                String songLrc = "";
                songList.add(new Music(songTitle, singerName, songLink, songPic, songLrc));

            }

            msg.what = 0;
            msg.obj = songList;
            mHandler.sendMessage(msg);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


   /* public String getJsonByGet(String address) {
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
    }*/
}
