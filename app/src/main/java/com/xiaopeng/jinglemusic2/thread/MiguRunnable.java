package com.xiaopeng.jinglemusic2.thread;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.xiaopeng.jinglemusic2.Music;
import com.xiaopeng.jinglemusic2.bean.MusicInfo;
import com.xiaopeng.jinglemusic2.model.search.SearchModel;
import com.xiaopeng.jinglemusic2.utils.NetworkUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author liujian
 * @date 2017/8/29
 */

public class MiguRunnable implements Runnable {
    private String mSongName;
    private SearchModel.LoadCallback mLoadCallback;


    public MiguRunnable(String songName, SearchModel.LoadCallback callback) {
        this.mLoadCallback = callback;
        this.mSongName = songName;

    }


    @Override
    public void run() {
        ArrayList<Music> songList = new ArrayList<>();
        try {
            Gson gson = new Gson();
            String requestUrl = "http://c.musicapp.migu.cn/MIGUM2.0/v1.0/content/search_suggest.do?&ua=Android_migu&version=5.0.7&text=" + mSongName + "&type=0";
            JsonObject jsonObject = new JsonParser().parse(NetworkUtil.getJsonByGet(requestUrl)).getAsJsonObject();
            JsonArray jsonArray = jsonObject.getAsJsonArray("songSuggests");
            List<MusicInfo> musicInfos = gson.fromJson(jsonArray, new TypeToken<List<MusicInfo>>() {
            }.getType());
            for (MusicInfo info : musicInfos) {
                String songTitle = info.getSongname() + "——" + info.getArtistname();
                String songId = info.getSongid();
                String requetUrlAnother = "http://music.migu.cn/webfront/player/findsong.do?itemid=" + songId + "&type=song";
                JsonObject jsonObjectAnother = new JsonParser().parse(NetworkUtil.getJsonByGet(requetUrlAnother)).getAsJsonObject();
                JsonArray jsonArrayAnother = jsonObjectAnother.getAsJsonArray("msg");
                JsonObject jsonMusic = jsonArray.get(0).getAsJsonObject();
                String songPic = jsonMusic.getAsJsonPrimitive("poster").getAsString();
                String songLink;
                if (!TextUtils.isEmpty(jsonMusic.getAsJsonPrimitive("hdmp3").getAsString())) {
                    songLink = jsonMusic.getAsJsonPrimitive("hdmp3").getAsString();
                } else {
                    songLink = jsonMusic.getAsJsonPrimitive("mp3").getAsString();
                }
                String songLrc = "";
                songList.add(new Music(songTitle, info.getArtistname(), songLink, songPic, songLrc));

            }

        if (mLoadCallback != null){
                mLoadCallback.onSuccess(songList);
        }

        } catch (Exception e) {
           if (mLoadCallback != null){
               mLoadCallback.onFailed(e);
           }
           e.printStackTrace();
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
