package com.xiaopeng.jinglemusic2.ui;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.xiaopeng.jinglemusic2.Music;
import com.xiaopeng.jinglemusic2.view.play.IPlayView;

import java.io.IOException;
import java.util.List;

/**
 * Created by liujian on 2017/8/3.
 */

public class PlayService extends IntentService implements IPlayView {
    private String name = "PlayMusicService";
    private static String PLAY_MUSIC = "com.jingle.musicplayer";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public PlayService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null){
            String action = intent.getAction();
            if (action.equals(PLAY_MUSIC)){
                String musicLink = intent.getStringExtra("musicLink");
                playMusic(musicLink);
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    public static void startPlayServiceByIntent(Context context,  String musicLink){
        Intent intent = new Intent(context, PlayService.class);
        intent.setAction(PLAY_MUSIC);
        intent.putExtra("musicLink",musicLink);
        context.startService(intent);
    }

    public void playMusic(String musicLink){

        try {

            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {

                }
            });

            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {


                }
            });

            mediaPlayer.reset();
            mediaPlayer.setDataSource(musicLink);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Intent intent = new Intent(PlayService.PLAY_MUSIC);
            intent.putExtra("duration", mediaPlayer.getDuration());
            sendBroadcast(intent);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"播放失败",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showPlay(int position) {

    }

    @Override
    public void showStop() {

    }

    @Override
    public void showSwitchMode(int mode) {

    }

    @Override
    public void showMusicList(List<Music> musics) {

    }

    @Override
    public void showDownload(List<Music> musics) {

    }
}
