package com.xiaopeng.jinglemusic2.model.search;

import com.xiaopeng.jinglemusic2.Config;
import com.xiaopeng.jinglemusic2.Music;
import com.xiaopeng.jinglemusic2.thread.BaiduFlacRunnable;
import com.xiaopeng.jinglemusic2.thread.BaiduMp3Runnable;
import com.xiaopeng.jinglemusic2.thread.CommonRunnable;
import com.xiaopeng.jinglemusic2.thread.EchoRunnable;
import com.xiaopeng.jinglemusic2.thread.MiguRunnable;
import com.xiaopeng.jinglemusic2.thread.YitingRunnable;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Date: 2019/3/23
 * Created by LiuJian
 *
 * @author LiuJian
 */

/**
 * searching model
 *
 * @author XP-PC-XXX
 */
public class SearchModel implements ISearchModel {


    private LoadCallback mLoadCallback;
    private ExecutorService mExecutorService;

    public SearchModel(LoadCallback mLoadCallback) {
        this.mLoadCallback = mLoadCallback;
        mExecutorService = Executors.newFixedThreadPool(1);

    }


    public void setLoadCallback(LoadCallback mLoadCallback) {
        this.mLoadCallback = mLoadCallback;
    }


    @Override
    public void loadMusicList(String songName, int resourceFlag) {

        switch (resourceFlag) {
            case Config.BAIDU_FLAC_FLAG:
                mExecutorService.execute(new BaiduFlacRunnable(songName, mLoadCallback));
                break;
            case Config.NETEASE_FLAG:
                mExecutorService.execute(new CommonRunnable(songName, "netease", mLoadCallback));
                break;
            case Config.QQ_FLAG:
                mExecutorService.execute(new CommonRunnable(songName, "qq", mLoadCallback));
                break;
            case Config.BAIDU_MP3_FLAG:
                mExecutorService.execute(new BaiduMp3Runnable(songName, mLoadCallback));
                break;
            case Config.XIAMI_FLAG:
                mExecutorService.execute(new CommonRunnable(songName, "xiami", mLoadCallback));
                break;
            case Config.KUGOU_FLAG:
                // mExecutorService.execute(new KugouRunnable(songName, mLoadCallback));
                mExecutorService.execute(new CommonRunnable(songName, "kugou", mLoadCallback));
                break;
            case Config.KUWO_FLAG:
                //mExecutorService.execute(new KuwoRunnable(songName, mLoadCallback));
                mExecutorService.execute(new CommonRunnable(songName, "kuwo", mLoadCallback));
                break;
            case Config.MIGU_FLAG:
                mExecutorService.execute(new MiguRunnable(songName, mLoadCallback));
                break;
            case Config.ECHO_FLAG:
                mExecutorService.execute(new EchoRunnable(songName, mLoadCallback));
                break;
            case Config.YITING_FLAG:
                mExecutorService.execute(new YitingRunnable(songName, mLoadCallback));
                break;
            default:
                mExecutorService.execute(new BaiduFlacRunnable(songName, mLoadCallback));
                break;
        }

    }

    public interface LoadCallback {
        void onSuccess(List<Music> musicList);

        void onFailed(Exception exception);

        void onProgress();

    }
}
