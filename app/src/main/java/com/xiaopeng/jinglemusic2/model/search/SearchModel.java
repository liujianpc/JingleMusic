package com.xiaopeng.jinglemusic2.model.search;

import com.xiaopeng.jinglemusic2.Music;
import com.xiaopeng.jinglemusic2.thread.BaiduFlacRunnable;
import com.xiaopeng.jinglemusic2.thread.BaiduMp3Runnable;
import com.xiaopeng.jinglemusic2.thread.CommonRunnable;
import com.xiaopeng.jinglemusic2.thread.EchoRunnable;
import com.xiaopeng.jinglemusic2.thread.KugouRunnable;
import com.xiaopeng.jinglemusic2.thread.KuwoRunnable;
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
            case 0:
                mExecutorService.execute(new BaiduFlacRunnable(songName, mLoadCallback));
                break;
            case 1:
                mExecutorService.execute(new CommonRunnable(songName, "netease", mLoadCallback));
                break;
            case 2:
                mExecutorService.execute(new CommonRunnable(songName, "qq", mLoadCallback));
                break;
            case 3:
                mExecutorService.execute(new BaiduMp3Runnable(songName, mLoadCallback));
                break;
            case 4:
                mExecutorService.execute(new CommonRunnable(songName, "xiami", mLoadCallback));
                break;
            case 5:
                mExecutorService.execute(new KugouRunnable(songName, mLoadCallback));
                break;
            case 6:
                mExecutorService.execute(new KuwoRunnable(songName, mLoadCallback));
                break;
            case 7:
                mExecutorService.execute(new MiguRunnable(songName, mLoadCallback));
                break;
            case 8:
                mExecutorService.execute(new EchoRunnable(songName, mLoadCallback));
                break;
            case 9:
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
