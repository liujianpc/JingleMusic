package com.xiaopeng.jinglemusic2.view.play;

import com.xiaopeng.jinglemusic2.Music;

import java.util.List;

/**
 * Date: 2019/3/23
 * Created by LiuJian
 *
 * @author LiuJian
 */

public interface IPlayActivityView {
    void showPlay(int position);

    void showStop();

    void showPause();

    void showSwitchMode(int mode);

    void showMusicList(List<Music> musics);

    void showDownload(List<Music> musics);

    void showPlay();


}