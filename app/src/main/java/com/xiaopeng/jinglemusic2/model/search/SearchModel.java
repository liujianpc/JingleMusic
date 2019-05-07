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
 *
 * @author XP-PC-XXX
 */
public class SearchModel implements ISearchModel {

    private static final int BAIDU_FLAC_FLAG = 0;
    private static final int NETEASE_FLAG = 1;
    private static final int QQ_FLAG = 2;
    private static final int BAIDU_MP3_FLAG = 3;
    private static final int XIAMI_FLAG = 4;
    private static final int KUGOU_FLAG = 5;
    private static final int KUWO_FLAG = 6;
    private static final int MIGU_FLAG = 7;
    private static final int ECHO_FLAG = 8;
    private static final int YITING_FLAG = 9;
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
            case BAIDU_FLAC_FLAG:
                mExecutorService.execute(new BaiduFlacRunnable(songName, mLoadCallback));
                break;
            case NETEASE_FLAG:
                mExecutorService.execute(new CommonRunnable(songName, "netease", mLoadCallback));
                break;
            case QQ_FLAG:
                mExecutorService.execute(new CommonRunnable(songName, "qq", mLoadCallback));
                break;
            case BAIDU_MP3_FLAG:
                mExecutorService.execute(new BaiduMp3Runnable(songName, mLoadCallback));
                break;
            case XIAMI_FLAG:
                mExecutorService.execute(new CommonRunnable(songName, "xiami", mLoadCallback));
                break;
            case KUGOU_FLAG:
                mExecutorService.execute(new KugouRunnable(songName, mLoadCallback));
                break;
            case KUWO_FLAG:
                mExecutorService.execute(new KuwoRunnable(songName, mLoadCallback));
                break;
            case MIGU_FLAG:
                mExecutorService.execute(new MiguRunnable(songName, mLoadCallback));
                break;
            case ECHO_FLAG:
                mExecutorService.execute(new EchoRunnable(songName, mLoadCallback));
                break;
            case YITING_FLAG:
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
