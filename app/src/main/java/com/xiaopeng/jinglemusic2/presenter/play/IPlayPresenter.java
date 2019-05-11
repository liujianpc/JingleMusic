package com.xiaopeng.jinglemusic2.presenter.play;

/**
 * Date: 2019/3/23
 * Created by LiuJian
 *
 * @author LiuJian
 */

public interface IPlayPresenter {

    //首次播放
    void firstPlay(int position);
    //播放
    void play();
    //暂停
    void pause();
    //停止播放
    void stop();
    //上一首
    int last();
    //下一首
    int next();
    //播放模式
    void playMode(int mode);
    //帶有位置的播放
    void playWithPosition(int position);
}
