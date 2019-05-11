package com.xiaopeng.jinglemusic2.helper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.xiaopeng.jinglemusic2.IPlayServiceInterface;
import com.xiaopeng.jinglemusic2.Music;
import com.xiaopeng.jinglemusic2.presenter.play.IPlayPresenter;
import com.xiaopeng.jinglemusic2.ui.App;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 2019/5/10
 * Created by LiuJian
 *
 * @author LiuJian
 */

public class PlayMusicHelper implements IPlayPresenter {

    private static final String TAG = "PlayMusicHelper";
    private IPlayServiceInterface mPlayService;

    private PlayMusicHelper() {
    }


    private static final class Holder {
        private static final PlayMusicHelper INSTANCE = new PlayMusicHelper();
    }

    public static PlayMusicHelper getInstance() {
        return Holder.INSTANCE;
    }

    private ServiceConnection mConnection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlayService = IPlayServiceInterface.Stub.asInterface(service);
            Log.d(TAG, "mPlayService--->" + mPlayService);


            IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                   // mPlayService.asBinder().unlinkToDeath(this, 0);
                    bindService(App.getInstance());
                }
            };

            try {
                mPlayService.asBinder().linkToDeath(deathRecipient, 0);
            } catch (RemoteException mE) {
                Log.e(TAG, "exception occurs,", mE);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPlayService = null;

        }
    };

    /**
     * 绑定service
     */
    public void initService(List<Music> musicList) {

        Intent intent = new Intent("com.xiaopeng.jinglemusic2.PlayMusicService");
        intent.putExtra("musicList", (ArrayList<Music>) musicList);
        intent.setPackage("com.xiaopeng.jinglemusic2");
        App.getInstance().startService(intent);
        bindService(App.getInstance());


    }

    private void bindService(Context context) {

        Intent intent = new Intent("com.xiaopeng.jinglemusic2.PlayMusicService");
        intent.setPackage("com.xiaopeng.jinglemusic2");
        context.startService(intent);
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void firstPlay(int position) {
        if (mPlayService != null) {
            try {
                mPlayService.firstPlay(position);
            } catch (RemoteException mE) {
                Log.e(TAG, "exception occurs,", mE);
            }
        }

    }

    @Override
    public void play() {
        if (mPlayService != null) {
            try {
                mPlayService.play();
            } catch (RemoteException mE) {
                Log.e(TAG, "exception occurs,", mE);
            }
        }

    }

    @Override
    public void pause() {
        if (mPlayService != null) {
            try {
                mPlayService.pause();
            } catch (RemoteException mE) {
                Log.e(TAG, "exception occurs,", mE);
            }
        }

    }

    @Override
    public void stop() {
        if (mPlayService != null) {
            try {
                mPlayService.stop();
            } catch (RemoteException mE) {
                Log.e(TAG, "exception occurs,", mE);
            }
        }

    }

    @Override
    public int last() {
        int position = 0;
        if (mPlayService != null) {
            try {
                position = mPlayService.last();
            } catch (RemoteException mE) {
                Log.e(TAG, "exception occurs,", mE);
            }
        }

        return position;

    }

    @Override
    public int next() {
        int position = 0;
        if (mPlayService != null) {
            try {
                position = mPlayService.next();
            } catch (RemoteException mE) {
                Log.e(TAG, "exception occurs,", mE);
            }
        }

        return position;

    }

    @Override
    public void playMode(int mode) {
        if (mPlayService != null) {
            try {
                mPlayService.playMode(mode);
            } catch (RemoteException mE) {
                Log.e(TAG, "exception occurs,", mE);
            }
        }

    }

    @Override
    public void playWithPosition(int position) {
        if (mPlayService != null) {
            try {
                mPlayService.playWithPosition(position);
            } catch (RemoteException mE) {
                Log.e(TAG, "exception occurs,", mE);
            }
        }

    }

}
