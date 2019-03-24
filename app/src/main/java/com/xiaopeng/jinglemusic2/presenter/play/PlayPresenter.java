package com.xiaopeng.jinglemusic2.presenter.play;

import com.xiaopeng.jinglemusic2.model.play.IPlayModel;
import com.xiaopeng.jinglemusic2.view.play.IPlayView;

/**
 * Date: 2019/3/24
 * Created by LiuJian
 *
 * @author LiuJian
 */

public class PlayPresenter implements IPlayPresenter {

    private IPlayView mPlayView;
    private IPlayModel mPlayModel;


    public PlayPresenter(IPlayView mPlayView, IPlayModel mPlayModel) {
        this.mPlayView = mPlayView;
        this.mPlayModel = mPlayModel;
    }

    @Override
    public void play(int position) {

    }

    @Override
    public void stop() {

    }

    @Override
    public void download() {

    }

    @Override
    public void loadMusicList() {



    }
}
