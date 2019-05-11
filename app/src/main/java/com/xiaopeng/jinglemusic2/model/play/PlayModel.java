package com.xiaopeng.jinglemusic2.model.play;

import com.xiaopeng.jinglemusic2.presenter.play.IPlayPresenter;

/**
 * Date: 2019/3/23
 * Created by LiuJian
 *
 * @author LiuJian
 */

public class PlayModel implements IPlayModel {

    private IPlayPresenter mPlayPresenter;

    public PlayModel(IPlayPresenter playPresenter) {

        this.mPlayPresenter = playPresenter;
    }

    @Override
    public void loadMusicList(String address) {

    }
}
