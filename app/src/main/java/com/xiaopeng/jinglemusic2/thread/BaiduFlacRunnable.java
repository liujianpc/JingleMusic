package com.xiaopeng.jinglemusic2.thread;

import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xiaopeng.jinglemusic2.Music;
import com.xiaopeng.jinglemusic2.utils.NetworkUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by liujian on 2017/8/11.
 */

public class BaiduFlacRunnable implements Runnable {
    private ArrayList<Music> songList;
    private Handler mHandler;
    private String songName;

    public BaiduFlacRunnable() {
        super();
    }

    public BaiduFlacRunnable(Handler mHandler, String songName) {
        this.mHandler = mHandler;
        this.songName = songName;
    }

    @Override
    public void run() {
        songList = new ArrayList<>();
        Message message = new Message();
        String baseUrl = "http://music.baidu.com/search?key=";

        Document document = null;
        try {
            document = Jsoup.connect(
                    baseUrl + songName).get();
            Elements div_song = document
                    .select("div.song-item.clearfix");
            for (Element element : div_song) {
                Element childElement = element.child(3)
                        .child(0);
                Element childElement2 = element.child(6).child(
                        0);
                if (childElement2.attr("title").equals("无损资源")) {

                    String song_id = childElement.attr("href")
                            .split("/")[2];
                    String requestUrl = "http://music.baidu.com/data/music/songlink?songIds="
                            + song_id
                            + "&hq=&type=flac&rate=&pt=0&flag=-1&s2p=-1&prerate=-1&bwt=-1&dur=-1&bat=-1&bp=-1&pos=-1&auto=-1";
                    String json = NetworkUtil.getJsonByGet(requestUrl);
                    Gson gson = new Gson();

                    JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
                    JsonArray jsonArray = jsonObject.getAsJsonObject("data").getAsJsonArray("songList");

                    Music music = gson.fromJson(jsonArray.get(0), Music.class);
                    songList.add(music);

                }

            }
            message.what = 0;
            message.obj = songList;
            mHandler.sendMessage(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            message.what = 1;
            mHandler.sendMessage(message);

        }

    /*public String getJson(String address) {
       String response = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(address);
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
}
