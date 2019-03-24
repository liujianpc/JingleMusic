package com.xiaopeng.jinglemusic2.presenter.play;

/**
 * Date: 2019/3/23
 * Created by LiuJian
 *
 * @author LiuJian
 */

public interface IPlayPresenter {
    void play(int position);

    void stop();

    void download();

    void loadMusicList();
}
