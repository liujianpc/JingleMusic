package com.xiaopeng.jinglemusic2.thread;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xiaopeng.jinglemusic2.Music;
import com.xiaopeng.jinglemusic2.model.search.SearchModel;
import com.xiaopeng.jinglemusic2.utils.NetworkUtil;

import java.util.ArrayList;

/**
 * Created by liujian on 2017/9/1.
 */

public class YitingRunnable implements Runnable {
    private static final String TAG = "YitingRunnable";
    private String mSongName;
    private SearchModel.LoadCallback mLoadCallback;


    public YitingRunnable(String songName, SearchModel.LoadCallback callback) {
        this.mLoadCallback = callback;
        this.mSongName = songName;

    }


    @Override
    public void run() {
        ArrayList<Music> songList = new ArrayList<>();
        try {
            String requestUrl = "http://so.1ting.com/song/json?q=" + mSongName + "&page=1";
            JsonObject jsonObject = new JsonParser().parse(NetworkUtil.getJsonByGet(requestUrl)).getAsJsonObject();
            JsonArray jsonArray = jsonObject.getAsJsonArray("results");
            for (JsonElement element : jsonArray) {
                JsonObject jsonObjectInner = element.getAsJsonObject();
                String singerName = jsonObjectInner.getAsJsonPrimitive("singer_name").getAsString();
                String songTitle = jsonObjectInner.getAsJsonPrimitive("song_name").getAsString() + "——" + singerName;
                String songPic = jsonObjectInner.getAsJsonPrimitive("album_cover").getAsString();
                String songLink = "http://96.1ting.com" + jsonObjectInner.getAsJsonPrimitive("song_filepath").getAsString().replace("wma", "mp3");
                String songLrc = "";
                songList.add(new Music(songTitle, singerName, songLink, songPic, songLrc));

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


   /* public String getJsonByGet(String address) {
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
