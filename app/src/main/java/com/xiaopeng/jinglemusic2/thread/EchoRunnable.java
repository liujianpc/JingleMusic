package com.xiaopeng.jinglemusic2.thread;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.xiaopeng.jinglemusic2.Music;
import com.xiaopeng.jinglemusic2.model.search.SearchModel;
import com.xiaopeng.jinglemusic2.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujian on 2017/8/29.
 */

public class EchoRunnable implements Runnable {
    private static final String TAG = "EchoRunnable";
    private String mSongName;
    private SearchModel.LoadCallback mLoadCallback;


    public EchoRunnable(String songName, SearchModel.LoadCallback callback) {
        this.mSongName = songName;
        this.mLoadCallback = callback;

    }


    @Override
    public void run() {
        ArrayList<Music> songList;
        try {
            String requestUrl = "http://www.app-echo.com/api/search/sound?keyword=" + mSongName + "&page=1&limit=10&src=0";
            JsonObject jsonObject = new JsonParser().parse(NetworkUtil.getJsonByGet(requestUrl)).getAsJsonObject();
            JsonArray jsonArray = jsonObject.getAsJsonArray("data");
            songList = new Gson().fromJson(jsonArray, new TypeToken<List<Music>>() {
            }.getType());


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

/*
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
    }*/
}
