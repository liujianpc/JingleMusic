package com.example.jinglemusic.thread;

import android.os.Handler;
import android.os.Message;

import com.example.jinglemusic.model.Music;
import com.example.jinglemusic.utils.JsonpUtil;
import com.example.jinglemusic.utils.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by liujian on 2017/8/13.
 */

public class QQMusicRunnable implements Runnable {
    private ArrayList<Music> songList;
    private Handler mHandler;
    private String songName;

    public QQMusicRunnable() {
        super();
    }

    public QQMusicRunnable(Handler mHandler, String songName) {
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
            String url = "http://s.music.qq.com/fcgi-bin/music_search_new_platform?t=0&n=5&aggr=1&cr=1&loginUin=0&format=json&inCharset=GB2312&outCharset=utf-8&notice=0&platform=jqminiframe.json&needNewCode=0&p=1&catZhida=0&remoteplace=sizer.newclient.next_song&w=" + URLEncoder.encode(songName, "UTF-8");
            JSONObject jsonObject = new JSONObject(NetworkUtil.getJsonByGet(url));
            JSONObject jsonSong = jsonObject.getJSONObject("data").getJSONObject("song");
            JSONArray jsonArray = jsonSong.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonInner = jsonArray.getJSONObject(i);
                String songName = jsonInner.getString("fsong") + "——" + jsonInner.getString("fsinger") + "——" + jsonInner.getString("albumName_hilight");
                String fString = jsonInner.getString("f");
                if (fString.contains("@")) continue;
                String[] fStrings = fString.split("\\|");
                String id = fStrings[20];
                String json = JsonpUtil.parseJSONP(NetworkUtil.getJsonByGet("http://base.music.qq.com/fcgi-bin/fcg_musicexpress.fcg?json=3&loginUin=0&format=jsonp&inCharset=GB2312&outCharset=GB2312&notice=0&platform=yqq&needNewCode=0"));
                JSONObject jsonObjectAnother = new JSONObject(json);
                String key = jsonObjectAnother.getString("key");
                String songLink = "http://cc.stream.qqmusic.qq.com/C100" + id + ".m4a?vkey=" + key + "&fromtag=0";
                String imgId = fStrings[22];
                String songPic = "http://imgcache.qq.com/music/photo/mid_album_90/" + imgId.charAt(imgId.length() - 2) + "/" + imgId.charAt(imgId.length() - 1) + "/" + imgId + ".jpg";
                String LrcId = fStrings[0];
                //String songLrc = "http://music.qq.com/miniportal/static/lyric/"+Integer.valueOf(LrcId)%100+"/"+LrcId+".xml";//xml歌词数据
                String songLrc = null;
                songList.add(new Music(songName, songLink, songPic, songLrc));
            }

            msg.what = 0;
            msg.obj = songList;
            mHandler.sendMessage(msg);


        } catch (UnsupportedEncodingException | JSONException e) {
            msg.what = 1;
            mHandler.sendMessage(msg);
        } catch (IOException e) {
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
    }*/
}
