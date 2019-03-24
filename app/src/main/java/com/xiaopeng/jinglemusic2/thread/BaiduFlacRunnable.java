package com.xiaopeng.jinglemusic2.thread;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xiaopeng.jinglemusic2.Music;
import com.xiaopeng.jinglemusic2.model.search.SearchModel;
import com.xiaopeng.jinglemusic2.utils.NetworkUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 *
 * @author liujian
 * @date 2017/8/11
 */

public class BaiduFlacRunnable implements Runnable {
    private String mSongName;
    private SearchModel.LoadCallback mLoadCallback;

    public BaiduFlacRunnable(String songName, SearchModel.LoadCallback callback) {

        this.mSongName = songName;
        this.mLoadCallback = callback;
    }

    @Override
    public void run() {
        ArrayList<Music> songList = new ArrayList<>();
        String baseUrl = "http://music.baidu.com/search?key=";

        Document document = null;
        try {
            document = Jsoup.connect(
                    baseUrl + mSongName).get();
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
                    JsonArray jsonArray = jsonObject.getAsJsonObject("data").getAsJsonArray("mSongList");

                    Music music = gson.fromJson(jsonArray.get(0), Music.class);
                    songList.add(music);

                }

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
}
