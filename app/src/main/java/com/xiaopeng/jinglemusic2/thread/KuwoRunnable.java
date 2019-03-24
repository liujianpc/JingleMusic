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
 *
 * @author liujian
 * @date 2017/8/28
 */

public class KuwoRunnable implements Runnable {

    private String mSongName;
    private SearchModel.LoadCallback mLoadCallback;


    public KuwoRunnable(String songName, SearchModel.LoadCallback callback) {
        this.mLoadCallback = callback;
        this.mSongName = songName;

    }


    @Override
    public void run() {
        ArrayList<Music> songList = new ArrayList<>();
        try {
            Gson gson = new Gson();
            String requestUrl = "http://search.kuwo.cn/r.s?all=" + mSongName + "&ft=music&itemset=web_2013&client=kt&pn=0&rn=5&rformat=json&encoding=utf8";
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
