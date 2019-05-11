package com.xiaopeng.jinglemusic2.presenter.play;

import com.xiaopeng.jinglemusic2.helper.PlayMusicHelper;
import com.xiaopeng.jinglemusic2.model.play.IPlayModel;
import com.xiaopeng.jinglemusic2.model.play.PlayModel;
import com.xiaopeng.jinglemusic2.view.play.IPlayActivityView;

/**
 * Date: 2019/3/24
 * Created by LiuJian
 *
 * @author LiuJian
 */

public class PlayPresenter implements IPlayPresenter {

    private IPlayActivityView mPlayView;
    private IPlayModel mPlayModel;


    public PlayPresenter(IPlayActivityView mPlayView) {
        this.mPlayView = mPlayView;
        this.mPlayModel = new PlayModel(this);
    }


    @Override
    public void firstPlay(int position) {
        PlayMusicHelper.getInstance().firstPlay(position);
        mPlayView.showPlay(position);

    }

    @Override
    public void play() {
        PlayMusicHelper.getInstance().play();
        mPlayView.showPlay();


    }

    @Override
    public void pause() {
        PlayMusicHelper.getInstance().pause();
        mPlayView.showPause();


    }

    @Override
    public void stop() {
        PlayMusicHelper.getInstance().stop();
        mPlayView.showStop();

    }

    @Override
    public int last() {
        int position = PlayMusicHelper.getInstance().last();
        mPlayView.showPlay(position);
        return position;

    }

    @Override
    public int next() {
        int position = PlayMusicHelper.getInstance().next();
        mPlayView.showPlay(position);
        return position;

    }

    @Override
    public void playMode(int mode) {
        PlayMusicHelper.getInstance().playMode(mode);
        mPlayView.showSwitchMode(mode);

    }

    @Override
    public void playWithPosition(int position) {
        PlayMusicHelper.getInstance().playWithPosition(position);
        mPlayView.showPlay(position);

    }
}
