package com.example.jinglemusic;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by liujian on 2017/8/3.
 */

public class PlayMusicService extends Service {
    private MediaPlayer mediaPlayer;
    private String musicLink, musicName;
    private int musicLength;
    private boolean isComplete;
    private long seekBarPosition;
    // private Intent intent;
    private Thread thread;


    private PlayMusicBinder binder = new PlayMusicBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        musicLink = intent.getStringExtra("musicLink");
        musicName = intent.getStringExtra("musicName");
        return binder;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = new MediaPlayer();
       /* mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                seekBarPosition = mp.getCurrentPosition();
                intent.putExtra("seekBarPosition",seekBarPosition);
                sendBroadcast(intent);


            }
        });*/

       /* mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                isComplete = true;
                intent.putExtra("isComplete",isComplete);
                sendBroadcast(intent);

            }
        });*/
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent intent = new Intent("com.jingle.getmusictime");
                intent.putExtra("isComplete", true);
                sendBroadcast(intent);

            }
        });
        /*mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                musicLength = mp.getDuration();
            }
        });*/

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

    class PlayMusicBinder extends Binder {
        public MediaPlayer getPalyer() {
            /*intent.putExtra("musicLength",musicLength);
            sendBroadcast(intent);*/
            return mediaPlayer;
        }

        public void getStartPlay() {
            thread = new Thread(new PlayRunnable(musicLink));//开始播放
            thread.start();
        }

        public PlayMusicService getService() {
            return PlayMusicService.this;
        }
    }


    class PlayRunnable implements Runnable {
        String url;

        //MediaPlayer mediaPlayer;
        PlayRunnable(String url) {
            this.url = url;
            //this.mediaPlayer = mediaPlayer;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void run() {
            try {

                mediaPlayer.reset();
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepare();
                mediaPlayer.start();
                Intent notificationIntent = new Intent(getApplicationContext(), PlayActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
                Notification.Builder builder = new Notification.Builder(getApplicationContext());
                builder.setSmallIcon(R.drawable.music_notifiaction).setTicker(musicName).setContentTitle(musicName).setContentText(musicName).setContentIntent(pendingIntent);
                builder.setWhen(SystemClock.currentThreadTimeMillis());
                startForeground(1, builder.build());
                Timer timer = new Timer();
                mSendBroadCast("musicLength", mediaPlayer.getDuration());

                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (mediaPlayer != null) {
                            if (mediaPlayer.isPlaying()) {
                                mSendBroadCast("seekBarPosition", mediaPlayer.getCurrentPosition());
                            }
                        }
                    }
                }, 0, 1000);
            } catch (IllegalStateException e) {
                Log.e("liujian", e.toString());
            } catch (IOException | IllegalArgumentException | SecurityException e) {
                Log.e("liujian", e.toString());
            }
        }
    }

    public void mSendBroadCast(String key, int value) {
        Intent intent = new Intent("com.jingle.getmusictime");
        intent.putExtra(key, value);
        sendBroadcast(intent);

    }
}
