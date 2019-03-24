package com.xiaopeng.jinglemusic2.thread;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.xiaopeng.jinglemusic2.Music;
import com.xiaopeng.jinglemusic2.bean.MusicInfo;
import com.xiaopeng.jinglemusic2.model.search.SearchModel;
import com.xiaopeng.jinglemusic2.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * @author liujian
 * @date 2017/8/28
 */

public class KugouRunnable implements Runnable {

    private static final String TAG = "KugouRunnable";
    private String songName;
    private SearchModel.LoadCallback mLoadCallback;

    public KugouRunnable(String songName, SearchModel.LoadCallback mLoadCallback) {
        this.songName = songName;
        this.mLoadCallback = mLoadCallback;

    }

    @Override
    public void run() {
        ArrayList<Music> songList = new ArrayList<>();
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
