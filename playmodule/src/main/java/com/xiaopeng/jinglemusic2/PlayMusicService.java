package com.xiaopeng.jinglemusic2;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author liujian
 * @date 2017/8/3
 */

public class PlayMusicService extends Service {
    private static final String TAG = "PlayMusicService";
    private static final String ACTION_GET_MUSIC_TIME = "com.jingle.getmusictime";
    private MediaPlayer mediaPlayer;
    private String musicLink, musicName;
    private int musicLength;
    private boolean isComplete;
    private int seekBarPosition;
    private Thread thread;
    private static List<Music> mMusicList;
    private int mPosition;
    /**
     * 0随机 1单曲循环 2列表循环
     */
    private int playMode = 2;

    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    @Nullable
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "PlayService OnCreate");

        mediaPlayer = new MediaPlayer();

        /**
         * 播放完成則直接继续播放下一首
         */
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playMusic();
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (thread != null) {
            thread.interrupt();
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

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void run() {
            try {

                mediaPlayer.reset();
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepare();
                mediaPlayer.start();
                /*Intent notificationIntent = new Intent(getApplicationContext(), PlayActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
                Notification.Builder builder = new Notification.Builder(getApplicationContext());
                builder.setSmallIcon(R.drawable.music_notifiaction).setTicker(musicName).setContentTitle(musicName).setContentText(musicName).setContentIntent(pendingIntent);
                builder.setWhen(SystemClock.currentThreadTimeMillis());
                startForeground(1, builder.build());*/
                Timer timer = new Timer();
                sendTime2Front("musicLength", mediaPlayer.getDuration());

                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (mediaPlayer != null) {
                            if (mediaPlayer.isPlaying()) {
                                seekBarPosition = mediaPlayer.getCurrentPosition();
                                sendTime2Front("seekBarPosition", seekBarPosition);
                            }
                        }
                    }
                }, 0, 1000);
            } catch (IllegalStateException e) {
                Log.e(TAG, e.toString());
            } catch (IOException | IllegalArgumentException | SecurityException e) {
                Log.e(TAG, e.toString());
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


    class IPlayServiceImpl extends IPlayServiceInterface.Stub {
        @Override
        public void firstPlay(int position) throws RemoteException {
            executorService.execute(new PlayRunnable(mMusicList.get(position).songLink));
            Log.d(TAG, "run first play and mMusicList size--->" + mMusicList.size());
        }

        @Override
        public void play() throws RemoteException {
            if (mediaPlayer != null) {
                // mediaPlayer.start();
                mediaPlayer.seekTo(seekBarPosition);
            }

        }

        @Override
        public void pause() throws RemoteException {
            if (mediaPlayer != null) {

                mediaPlayer.pause();
            }
        }

        @Override
        public void stop() throws RemoteException {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

        }

        @Override
        public void last() throws RemoteException {
            executorService.execute(new PlayRunnable(mMusicList.get((--mPosition) % mMusicList.size()).songLink));
            sendPosition2Front(mPosition);

        }

        @Override
        public void next() throws RemoteException {
            executorService.execute(new PlayRunnable(mMusicList.get((++mPosition) % mMusicList.size()).songLink));
            sendPosition2Front(mPosition);
        }

        @Override
        public void playMode(int mode) throws RemoteException {

            playMode = mode;
        }

        @Override
        public void playWithPosition(int position) throws RemoteException {

        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
            try {
                super.onTransact(code, data, reply, flags);
            } catch (RuntimeException e) {
                Log.w("MyClass", "Unexpected remote exception", e);
                throw e;
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.w("MyClass", "Unexpected remote exception", e);
            }
            return false;
        }
    }

    /**
     * 根据mode来产生需要播放的position
     */
    private void playMusic() {
        switch (playMode) {

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
        executorService.execute(new PlayRunnable(mMusicList.get(mPosition).songLink));
    }
}
