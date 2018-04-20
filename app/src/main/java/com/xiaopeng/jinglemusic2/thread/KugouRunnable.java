package com.xiaopeng.jinglemusic2.thread;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

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
 * @author liujian
 * @date 2017/8/28
 */

public class KugouRunnable implements Runnable {

    private static final String TAG = "KugouRunnable";
    private ArrayList<Music> songList;
    private Handler mHandler;
    private String songName;

    public KugouRunnable() {
        super();
    }

    public KugouRunnable(Handler mHandler, String songName) {
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
            String requestUrl = "http://mobilecdn.kugou.com/api/v3/search/song?format=json&keyword=" + songName + "&page=1&pagesize=12";
            JsonObject jsonObject = new JsonParser().parse(NetworkUtil.getJsonByGet(requestUrl)).getAsJsonObject();

            JsonArray jsonArray = jsonObject.getAsJsonObject("data").getAsJsonArray("info");
            List<MusicInfo> MusicInfos = gson.fromJson(jsonArray, new TypeToken<List<MusicInfo>>() {
            }.getType());
            for (MusicInfo info : MusicInfos
                    ) {
                String hash = info.getHash();
                String requetUrlAnother = "http://m.kugou.com/app/i/getSongInfo.php?hash=" + hash + "&cmd=playInfo";
                JsonObject jsonObj = new JsonParser().parse(NetworkUtil.getJsonByGet(requetUrlAnother)).getAsJsonObject();
                String songName = jsonObj.getAsJsonPrimitive("songName").getAsString();
                String singerName = jsonObj.getAsJsonPrimitive("singerName").getAsString();
                String fileName = jsonObj.getAsJsonPrimitive("fileName").getAsString();
                String pic = jsonObj.getAsJsonPrimitive("imgUrl").getAsString().replace("{size}", "120");
                String songLink = jsonObj.getAsJsonPrimitive("url").getAsString();


                Music music = new Music(fileName, singerName, songLink, pic, "");
                songList.add(music);

            }

            msg.what = 0;
            msg.obj = songList;
            mHandler.sendMessage(msg);

        } catch (IOException e) {
            Log.e(TAG, e.toString());
            msg.what = 1;
            mHandler.sendMessage(msg);
        }

    }


    /*public String getJsonByGet(String address) {
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
    }*/
}
