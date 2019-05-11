package com.xiaopeng.jinglemusic2;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author liujian
 * @date 2017/8/3
 */

public class PlayMusicService extends Service {
    private static final String TAG = "PlayMusicService";
    private static final String ACTION_GET_MUSIC_TIME = "com.jingle.getmusictime";
    private MediaPlayer mMediaPlayer;
    private String musicLink, musicName;
    private int musicLength;
    private boolean isComplete;
    private int mSeekBarPosition;
    private static List<Music> mMusicList;
    private int mPosition;
    /**
     * 0随机 1单曲循环 2列表循环
     */
    private int mPlayMode = 2;

    private ExecutorService mExecutorService = Executors.newFixedThreadPool(1);

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        mMusicList = intent.getParcelableArrayListExtra("musicList");
        return (IBinder) new IPlayServiceImpl();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind");
        super.onRebind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "PlayService OnCreate");

        mMediaPlayer = new MediaPlayer();

        /**
         * 播放完成則直接继续播放下一首
         */
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playMusic();
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

    }


    /**
     * 播放綫程
     */
    class PlayRunnable implements Runnable {
        String url;

        PlayRunnable(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            try {

                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(url);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                /*Intent notificationIntent = new Intent(getApplicationContext(), PlayActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
                Notification.Builder builder = new Notification.Builder(getApplicationContext());
                builder.setSmallIcon(R.drawable.music_notifiaction).setTicker(musicName).setContentTitle(musicName).setContentText(musicName).setContentIntent(pendingIntent);
                builder.setWhen(SystemClock.currentThreadTimeMillis());
                startForeground(1, builder.build());*/

                ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
                sendTime2Front("musicLength", mMediaPlayer.getDuration());

                scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        if (mMediaPlayer != null) {
                            if (mMediaPlayer.isPlaying()) {
                                mSeekBarPosition = mMediaPlayer.getCurrentPosition();
                                sendTime2Front("seekBarPosition", mSeekBarPosition);
                            }
                        }
                    }
                }, 0, 1000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                Log.e(TAG, "exception occurs:", e);
            }
        }
    }

    /**
     * 发广播方法,发送进度条时间给前台
     *
     * @param key
     * @param value
     */
    public void sendTime2Front(String key, int value) {
        Intent intent = new Intent(Config.GET_MUSIC_TIME);
        intent.putExtra(key, value);
        sendBroadcast(intent);

    }

    /**
     * 发广播方法,发送歌曲位置给前台
     *
     * @param value
     */
    public void sendPosition2Front(int value) {
        Intent intent = new Intent(Config.GET_MUSIC_POSI);
        intent.putExtra("position", value);
        sendBroadcast(intent);

    }


    /**
     * 真正的binder实现类
     */
    class IPlayServiceImpl extends IPlayServiceInterface.Stub {
        @Override
        public void firstPlay(int position) throws RemoteException {
            mExecutorService.execute(new PlayRunnable(mMusicList.get(position).songLink));
            Log.d(TAG, "run first play and mMusicList size--->" + mMusicList.size());
        }

        @Override
        public void play() throws RemoteException {
            if (mMediaPlayer != null) {
                // mMediaPlayer.start();
                mMediaPlayer.seekTo(mSeekBarPosition);
            }

        }

        @Override
        public void pause() throws RemoteException {
            if (mMediaPlayer != null) {

                mMediaPlayer.pause();
            }
        }

        @Override
        public void stop() throws RemoteException {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
            }

        }

        @Override
        public int last() throws RemoteException {
            int position = (--mPosition) % mMusicList.size();
            mExecutorService.execute(new PlayRunnable(mMusicList.get(position).songLink));
            sendPosition2Front(mPosition);
            return position;

        }

        @Override
        public int next() throws RemoteException {
            int position = (++mPosition) % mMusicList.size();
            mExecutorService.execute(new PlayRunnable(mMusicList.get(position).songLink));
            sendPosition2Front(mPosition);
            return position;
        }

        @Override
        public void playMode(int mode) throws RemoteException {

            mPlayMode = mode;
        }

        @Override
        public void playWithPosition(int position) throws RemoteException {

        }

    }

    /**
     * 根据mode来产生需要播放的position
     */
    private void playMusic() {
        switch (mPlayMode) {

            //随机
            case 0:
                mPosition = (int) (Math.random() * mMusicList.size());
                break;

            //单曲循环,position不做任何處理
            case 1:

                break;
            //列表循环
            case 2:
                mPosition = (++mPosition) % mMusicList.size();
                break;
            default:
                break;

        }

        sendPosition2Front(mPosition);
        mExecutorService.execute(new PlayRunnable(mMusicList.get(mPosition).songLink));
    }
}
