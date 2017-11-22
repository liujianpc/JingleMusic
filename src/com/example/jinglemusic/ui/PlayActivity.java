package com.example.jinglemusic.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.jinglemusic.model.DownLoadMusic;
import com.example.jinglemusic.control.DownLoadMusicAdapter;
import com.example.jinglemusic.utils.FastBlurUtil;
import com.example.jinglemusic.model.Music;
import com.example.jinglemusic.control.MusicListAdapter;
import com.example.jinglemusic.R;
import com.example.jinglemusic.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.SimpleDListener;

public class PlayActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ImageButton playModel, lastSong, playAndPause, nextSong, songList, download, downloadList;
    private ArrayList<Music> musicList;
    private SeekBar seekBar;
    private ImageView musicImage;
    private Animation animation;
    private TextView musicTime;
    private Intent bindIntent;
    private int position;
    private int clickCount = 0;
    private int playModeFlag = 2;
    private boolean isPlaying = true;
    private boolean isComplete;
    private IntentFilter intentFilter;
    private PlayMusicReceiver receiver;
    private int time;
    private int seekBarposition;
    private ListView musicListView, downLoadListPopup;
    private TextView musicName, playedMusicTime;
    private PopupWindow popupWindow;
    private ImageView backGround;
    private PlayMusicService.PlayMusicBinder binder;

    // private PlayMusicService playMusicService;
    private MediaPlayer mediaPlayer;

    private List<DownLoadMusic> downloadMusicList = new ArrayList<>();
    private int downloadProgress;
    private int fileSize;
    private DownLoadMusicAdapter adapter;
    private HashMap<String, Integer> progressMap = new HashMap<>();
    private List<String> downLoadMusicName = new ArrayList<>();


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (PlayMusicService.PlayMusicBinder) service;
            binder.getStartPlay();
            mediaPlayer = binder.getPalyer();
            // playMusicService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //  playMusicService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_play);
        initView();
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        musicList = (ArrayList<Music>) intent.getSerializableExtra("musicList");
        initPlayer();
        receiver = new PlayMusicReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.jingle.getmusictime");
        registerReceiver(receiver, intentFilter);

    }

    public void initView() {
        backGround = (ImageView) findViewById(R.id.back_ground);
        playModel = (ImageButton) findViewById(R.id.play_model);
        lastSong = (ImageButton) findViewById(R.id.last_song);
        playAndPause = (ImageButton) findViewById(R.id.play_pause);
        nextSong = (ImageButton) findViewById(R.id.next_song);
        songList = (ImageButton) findViewById(R.id.song_list);
        download = (ImageButton) findViewById(R.id.download);
        downloadList = (ImageButton) findViewById(R.id.download_list);
        seekBar = (SeekBar) findViewById(R.id.music_seekbar);
        musicImage = (ImageView) findViewById(R.id.music_image);
        musicName = (TextView) findViewById(R.id.music_name);
        musicTime = (TextView) findViewById(R.id.music_time);
        playedMusicTime = (TextView) findViewById(R.id.music_played_time);
        playModel.setOnClickListener(this);
        lastSong.setOnClickListener(this);
        playAndPause.setOnClickListener(this);
        nextSong.setOnClickListener(this);
        songList.setOnClickListener(this);
        download.setOnClickListener(this);
        downloadList.setOnClickListener(this);
        /*seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mediaPlayer.seekTo(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });*/
    }

    public void initPlayer() {
        musicName.setText(musicList.get(position).getSongTitle());
        bindIntent = new Intent(this, PlayMusicService.class);
        bindIntent.putExtra("musicName",musicList.get(position).songTitle);
        bindIntent.putExtra("musicLink", musicList.get(position).getSongLink());
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);//开始绑定service

        isPlaying = true;
        if (musicList.get(position).getSongPic() != null) {
            new Thread(new MyThread()).start();
        }
        animation = AnimationUtils.loadAnimation(this, R.anim.my_anim);
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        animation.setInterpolator(linearInterpolator);
        musicImage.startAnimation(animation);
    }

    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        int songListClickCount = 0;
        switch (view.getId()) {
            case R.id.play_model:
                clickCount++;
                switch (clickCount % 3) {
                    case 0://随机播放
                        ToastUtil.showToast(this, "随机播放");
                        playModel.setBackground(getResources().getDrawable(R.drawable.random));
                        playModeFlag = 0;
                        break;
                    case 1://单曲循环
                        ToastUtil.showToast(this, "单曲循环");
                        playModel.setBackground(getResources().getDrawable(R.drawable.single_recycle));
                        playModeFlag = 1;
                        break;
                    case 2://列表循环
                        ToastUtil.showToast(this, "列表循环");
                        playModel.setBackground(getResources().getDrawable(R.drawable.list_recycle));
                        playModeFlag = 2;
                        break;
                }
                break;
            case R.id.last_song:
                if (playModeFlag != 0) {
                    if (position > 0) {
                        position--;
                    } else {
                        position = musicList.size() - 1;
                    }
                } else {
                    position = (int) (Math.random() * musicList.size());
                }
                isPlaying = true;
                unbindService(serviceConnection);
                // stopService(bindIntent);
                bindIntent.putExtra("musicName",musicList.get(position).songTitle);
                bindIntent.putExtra("musicLink", musicList.get(position).getSongLink());
                bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);


                if (musicList.get(position).getSongPic() != null) {
                    new Thread(new MyThread()).start();
                }
                //  new Thread(new PlayRunnable(musicList.get(position).getSongLink(),mediaPlayer)).start();
                musicName.setText(musicList.get(position).getSongTitle());
                break;
            case R.id.play_pause:
                if (mediaPlayer.isPlaying()) {
                    musicImage.clearAnimation();
                    mediaPlayer.pause();
                    playAndPause.setSelected(true);
                    /*unbindService(serviceConnection);
                    stopService(bindIntent);*/
                    //playAndPause.setBackground(getResources().getDrawable(R.drawable.pause));
                    //mediaPlayer.pause();
                } else {
                    playAndPause.setSelected(false);
                    musicImage.startAnimation(animation);
                    mediaPlayer.start();
                    /*isPlaying = true;
                    bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);*/
                    //  playAndPause.setBackground(getResources().getDrawable(R.drawable.play));
                }

                break;
            case R.id.next_song:
                if (playModeFlag != 0) {
                    if (position < musicList.size() - 1) {
                        position++;
                    } else {
                        position = 0;
                    }
                } else {
                    position = (int) (Math.random() * musicList.size());
                }
                isPlaying = true;
                //  new Thread(new PlayRunnable(musicList.get(position).getSongLink(), mediaPlayer)).start();
                unbindService(serviceConnection);
                bindIntent.putExtra("musicName",musicList.get(position).songTitle);
                bindIntent.putExtra("musicLink", musicList.get(position).getSongLink());
                bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);

                if (musicList.get(position).getSongPic() != null) {
                    new Thread(new MyThread()).start();
                }
                musicName.setText(musicList.get(position).getSongTitle());
                break;
            case R.id.song_list:
                songListClickCount++;
                if (songListClickCount % 2 == 0) {
                    if (popupWindow != null) {
                        popupWindow.dismiss();
                    }
                } else {
                    View musicListPopup = LayoutInflater.from(PlayActivity.this).inflate(R.layout.music_list_popupwindow, null);
                    musicListView = (ListView) musicListPopup.findViewById(R.id.music_list);
                    MusicListAdapter adapter = new MusicListAdapter(PlayActivity.this, R.layout.music_list_item, musicList);
                    musicListView.setAdapter(adapter);
                    musicListView.setOnItemClickListener(this);
                    popupWindow = new PopupWindow(musicListPopup, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                    ColorDrawable colorDrawable = new ColorDrawable();
                    popupWindow.setBackgroundDrawable(colorDrawable);
                    popupWindow.showAsDropDown(musicName, 0, 0);
                    popupWindow.setOutsideTouchable(true);
                }
                break;
            case R.id.download:
                DLManager dlManager = DLManager.getInstance(PlayActivity.this);
                dlManager.setDebugEnable(true);
                String songLink = musicList.get(position).getSongLink();
                String fileType = null;
                if (songLink.contains("mp3") || songLink.contains("MP3")) {
                    fileType = ".mp3";
                } else if (songLink.contains("m4a") || songLink.contains("M4A")) {
                    fileType = ".m4a";
                } else if (songLink.contains("wma") || songLink.contains("WMA")) {
                    fileType = ".wma";
                } else if (songLink.contains("mp4") || songLink.contains("MP4")) {
                    fileType = ".mp4";
                } else if (songLink.contains("acc") || songLink.contains("ACC")) {
                    fileType = ".acc";
                } else if (songLink.contains("flac") || songLink.contains("FLAC")) {
                    fileType = ".flac";
                }
                dlManager.dlStart(musicList.get(position).getSongLink(), Environment.getExternalStorageDirectory().getAbsolutePath(), musicList.get(position).getSongTitle() + fileType, new SimpleDListener() {
                    @Override
                    public void onPrepare() {
                        progressMap.put(musicList.get(position).songTitle,0);
                        super.onPrepare();
                    }

                    @Override
                    public void onStart(String fileName, String realUrl, int fileLength) {
                        progressMap.put(musicList.get(position).songTitle, 0);
                        ToastUtil.showToast(PlayActivity.this, "下载开始");
                        fileSize = fileLength;

                    }

                    @Override
                    public void onProgress(final int progress, String fileName) {
                        downloadProgress = fileSize > 0 ? (int) (progress * 100.0 / fileSize) : 0;
                        downloadProgress = downloadProgress> 100 ? 100 : downloadProgress;
                        progressMap.put(fileName.split("\\.")[0], downloadProgress);
                        if (adapter != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i < adapter.getCount(); i++) {
                                        String musicName = adapter.getItem(i).musicName;
                                        int progressFinal;
                                        if(progressMap != null){
                                             progressFinal = progressMap.get(musicName);
                                        }else {
                                            progressFinal = 0;
                                        }

                                        adapter.updateView(i, downLoadListPopup, progressFinal + "%");
                                    }

                                }
                            });

                        }

                    }

                    @Override
                    public void onFinish(File file) {
                        super.onFinish(file);
                    }

                    @Override
                    public void onError(int status, String error) {
                        super.onError(status, error);
                    }
                });
/*                dlManager.dlStart(musicList.get(position).getSongLink(), Environment.getExternalStorageDirectory().getAbsolutePath(), musicList.get(position).getSongTitle() + fileType, new IDListener() {
                    @Override
                    public void onPrepare() {
                        ToastUtil.showToast(PlayActivity.this, "准备下载");

                    }

                    @Override
                    public void onStart(String fileName, String realUrl, int fileLength) {
                        ToastUtil.showToast(PlayActivity.this, "下载开始");
                        fileSize = fileLength;

                    }

                    @Override
                    public void onProgress(int progress) {
                        downloadProgress = fileSize > 0 ? progress * 100 / fileSize : 0;
                       *//* adapter = (DownLoadMusicAdapter) downLoadListPopup.getAdapter();
                        adapter.clear();
                        downloadMusicList.add(new DownLoadMusic(musicList.get(position).getSongTitle(), downloadProgress + "%"));
                        adapter.addAll(downloadMusicList);
                        adapter.notifyDataSetChanged();*//*

                     *//*  new Thread(new Runnable() {
                           @Override
                           public void run() {
                               runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       adapter = (DownLoadMusicAdapter) downLoadListPopup.getAdapter();
                                       adapter.clear();
                                       downloadMusicList.add(new DownLoadMusic(musicList.get(position).getSongTitle(), downloadProgress + "%"));
                                       adapter.addAll(downloadMusicList);
                                       downLoadListPopup.setAdapter(adapter);
                                       adapter.notifyDataSetChanged();
                                   }
                               });
                           }
                       }).start();*//*
                    }

                    @Override
                    public void onStop(int progress) {
                        // ToastUtil.showToast(PlayActivity.this, "下载停止");
                    }

                    @Override
                    public void onFinish(File file) {
                        ToastUtil.showToast(PlayActivity.this, "下载完成！");
                    }

                    @Override
                    public void onError(int status, String error) {
//                        ToastUtil.showToast(PlayActivity.this, "下载错误！");
                        Log.e("liujian", status + error);
                    }
                });*/
                //    if (downloadMusicList.size() > 0 && downloadMusicList.contains())
                if (!downLoadMusicName.contains(musicList.get(position).getSongTitle())) {
                    downLoadMusicName.add(musicList.get(position).getSongTitle());
                    downloadMusicList.add(new DownLoadMusic(musicList.get(position).getSongTitle(), downloadProgress + "%"));
                }
                break;
            case R.id.download_list:
                View downLoadMusicListPopup = LayoutInflater.from(PlayActivity.this).inflate(R.layout.download_musiclist_popupwindow, null);
                downLoadListPopup = (ListView) downLoadMusicListPopup.findViewById(R.id.download_musiclist);
                adapter = new DownLoadMusicAdapter(PlayActivity.this, R.layout.download_music_item, downloadMusicList);
                downLoadListPopup.setAdapter(adapter);
                //adapter.updateView(0,downLoadListPopup,downloadProgress+"%");
                PopupWindow popupWindow = new PopupWindow(downLoadMusicListPopup, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                ColorDrawable colorDrawable = new ColorDrawable();
                popupWindow.setBackgroundDrawable(colorDrawable);
                popupWindow.showAsDropDown(downloadList, 0, 0);
                popupWindow.setOutsideTouchable(true);

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        unregisterReceiver(receiver);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public String getFormatTimeString(long mills) {
        int min = (int) (mills / 60000);
        int sec = (int) ((mills / 1000) % 60);
        String minString;
        if (min >= 0 && min < 10) {
            minString = "0" + min;
        } else {
            minString = String.valueOf(min);
        }
        String secString;
        if (sec >= 0 && sec < 10) {
            secString = "0" + sec;
        } else {
            secString = String.valueOf(sec);
        }
        return minString + ":" + secString;


    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int positionofMethod, long id) {
        popupWindow.dismiss();
        position = positionofMethod;
        String songLink = musicList.get(position).songLink;
        musicName.setText(musicList.get(position).songTitle);
        unbindService(serviceConnection);
        bindIntent.putExtra("musicLink", songLink);
        bindIntent.putExtra("musicName",musicList.get(position).songTitle);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);

        if (musicList.get(position).getSongPic() != null) {
            new Thread(new MyThread()).start();
        }
    }

    class PlayMusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getIntExtra("musicLength", 0) != 0) {
                    time = intent.getIntExtra("musicLength", 0);
                    musicTime.setText(getFormatTimeString(time));
                }
                if (intent.getIntExtra("seekBarPosition", 0) != 0) {
                    seekBarposition = intent.getIntExtra("seekBarPosition", 0);
                    playedMusicTime.setText(getFormatTimeString(seekBarposition));
                    seekBar.setProgress((int) (seekBar.getMax() * seekBarposition / time));

                }

                isComplete = intent.getBooleanExtra("isComplete", false);

                if (isComplete) {
                    unbindService(serviceConnection);
                    switch (playModeFlag) {
                        case 0://随机
                            position = (int) (Math.random() * musicList.size());
                            break;
                        case 1://单曲循环
                            break;
                        case 2://列表循环
                            if (position < musicList.size() - 1) {
                                position++;
                            } else {
                                position = 0;
                            }
                            break;
                    }
                    bindIntent.putExtra("musicName",musicList.get(position).songTitle);
                    bindIntent.putExtra("musicLink", musicList.get(position).getSongLink());
                    bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);

                    if (musicList.get(position).getSongPic() != null) {
                        new Thread(new MyThread()).start();
                    }
                    musicName.setText(musicList.get(position).songTitle);

                }
                Log.e("liujian", seekBarposition + "");
                Log.e("liujian", "time is" + time + "");

            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    /**
     * Generate a new description for this activity.  This method is called
     * before pausing the activity and can, if desired, return some textual
     * description of its current state to be displayed to the user.
     * <p>
     * <p>The default implementation returns null, which will cause you to
     * inherit the description from the previous activity.  If all activities
     * return null, generally the label of the top activity will be used as the
     * description.
     *
     * @return A description of what the user is doing.  It should be short and
     * sweet (only a few words).
     * @see #onCreateThumbnail
     * @see #onSaveInstanceState
     * @see #onPause
     */
    @Nullable
    @Override
    public CharSequence onCreateDescription() {
        return super.onCreateDescription();
    }

    class MyThread implements Runnable {
        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            final Bitmap bitmap = FastBlurUtil.GetUrlBitmap(musicList.get(position).getSongPic(), 3);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    backGround.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    backGround.setImageBitmap(bitmap);
                }
            });
        }
    }

}
