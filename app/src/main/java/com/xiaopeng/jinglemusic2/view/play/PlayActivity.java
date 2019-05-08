package com.xiaopeng.jinglemusic2.view.play;

import android.annotation.SuppressLint;
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
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
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

import com.bumptech.glide.Glide;
import com.xiaopeng.jinglemusic2.Config;
import com.xiaopeng.jinglemusic2.IPlayServiceInterface;
import com.xiaopeng.jinglemusic2.Music;
import com.xiaopeng.jinglemusic2.R;
import com.xiaopeng.jinglemusic2.control.DownLoadMusicAdapter;
import com.xiaopeng.jinglemusic2.control.MusicListAdapter;
import com.xiaopeng.jinglemusic2.model.DownLoadMusic;
import com.xiaopeng.jinglemusic2.ui.BaseActivity;
import com.xiaopeng.jinglemusic2.utils.FastBlurUtil;
import com.xiaopeng.jinglemusic2.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.SimpleDListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author XP-PC-XXX
 */
public class PlayActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, IPlayView {

    private static final String TAG = "PlayActivity";
    private IPlayServiceInterface mPlayService;
    private ImageButton playModel, lastSong, playAndPause, nextSong, songList, download, downloadList;
    private static List<Music> musicList;
    private SeekBar mSeekBar;
    private CircleImageView mMusicImage;
    private Animation mAnimation;
    private TextView mMusicTime;
    private Intent mBindIntent;
    private int mPosition;
    private int mClickCount = 0;
    private int mPlayModeFlag = 2;
    private boolean mIsPlaying = true;
    private boolean mIsComplete;
    private PlayMusicReceiver receiver;
    private int time;
    private ListView mMusicListView, mDownLoadListPopup;
    private TextView mMusicName, mPlayedMusicTime;
    private PopupWindow mPopupWindow;
    private ImageView mBackGround;

    private List<DownLoadMusic> mDownLoadMusicList = new ArrayList<>();
    private int mDownloadProgress;
    private int mFileSize;
    private DownLoadMusicAdapter mDownLoadMusicAdapter;
    private HashMap<String, Integer> mProgressMap = new HashMap<>();
    private List<String> mDownLoadMusicName = new ArrayList<>();


    private ExecutorService mExecutorService = Executors.newFixedThreadPool(1);


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlayService = IPlayServiceInterface.Stub.asInterface(service);
            Log.d(TAG, "mPlayService--->" + mPlayService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPlayService = null;

        }
    };

    /**
     * 绑定service
     */
    private void initService() {

        final Intent intent = new Intent("com.xiaopeng.jinglemusic2.PlayMusicService");
        intent.putExtra("musicList", (ArrayList<Music>) musicList);
        intent.setPackage("com.xiaopeng.jinglemusic2");
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        mIsPlaying = true;


    }

    /**
     * 點擊列表產生的播放
     *
     * @param position 點擊位置
     */
    private void playWithPosition(int position) {
        if (mPlayService != null) {
            try {
                mPlayService.playWithPosition(position);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 播放音樂
     */
    private void play() {
        if (mPlayService != null) {
            try {
                mPlayService.play();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 暫停播放
     */
    private void pause() {
        if (mPlayService != null) {
            try {
                mPlayService.pause();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 停止播放
     */
    private void stop() {
        if (mPlayService != null) {
            try {
                mPlayService.stop();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 播放上一首
     */
    private void last() {
        if (mPlayService != null) {
            try {
                mPlayService.last();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 播放下一首
     */
    private void next() {
        if (mPlayService != null) {
            try {
                mPlayService.next();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 第一次播放
     */
    private void firstPlay(int position) {
        if (mPlayService != null) {
            try {
                Log.d(TAG, "enter firtst play");
                mPlayService.firstPlay(position);
                mIsPlaying = true;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_play);

        Intent intent = getIntent();
        mPosition = intent.getIntExtra("mPosition", 0);
        musicList = intent.getParcelableArrayListExtra("musicList");
        initView();
        initService();
        //initPlayer();
        receiver = new PlayMusicReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.jingle.getmusictime");
        intentFilter.addAction("com.jingle.getmusictposition");
        registerReceiver(receiver, intentFilter);

    }

    /**
     * View的初始化
     */
    public void initView() {
        mBackGround = (ImageView) findViewById(R.id.back_ground);
        playModel = (ImageButton) findViewById(R.id.play_model);
        lastSong = (ImageButton) findViewById(R.id.last_song);
        playAndPause = (ImageButton) findViewById(R.id.play_pause);
        nextSong = (ImageButton) findViewById(R.id.next_song);
        songList = (ImageButton) findViewById(R.id.song_list);
        download = (ImageButton) findViewById(R.id.download);
        downloadList = (ImageButton) findViewById(R.id.download_list);
        mSeekBar = (SeekBar) findViewById(R.id.music_seekbar);
        mMusicImage = (CircleImageView) findViewById(R.id.music_image);
        mMusicName = (TextView) findViewById(R.id.music_name);
        mMusicTime = (TextView) findViewById(R.id.music_time);
        mPlayedMusicTime = (TextView) findViewById(R.id.music_played_time);
        playModel.setOnClickListener(this);
        lastSong.setOnClickListener(this);
        playAndPause.setOnClickListener(this);
        nextSong.setOnClickListener(this);
        songList.setOnClickListener(this);
        download.setOnClickListener(this);
        downloadList.setOnClickListener(this);

    }

    /**
     * 判断playService是否连接成功
     *
     * @return
     */
    private boolean isServiceConnected() {
        Log.d(TAG, "mPlayService--->" + (mPlayService == null ? "null" : " not null"));
        return mPlayService != null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPlayer();

    }

    /**
     * 初始化音樂播放器
     */
    public void initPlayer() {

        //初始化，第一次播放
        Log.d(TAG, "isServiceConnected --->" + isServiceConnected());

        firstPlay(mPosition);

        initPlayerView(mPosition);
    }

    /**
     * 初始化播放器界面
     */
    @SuppressLint("ResourceType")
    private void initPlayerView(int position) {

        if (!TextUtils.isEmpty(musicList.get(position).getSongPic())) {

            Glide.with(this).load(R.drawable.music_play_cover).centerCrop().into(mMusicImage);

        } else {
            Glide.with(this).load(musicList.get(position).getSongPic()).centerCrop().into(mMusicImage);

        }
        mMusicName.setText(musicList.get(position).getSongTitle());
        //渲染高斯模糊背景
        mIsPlaying = true;
        if (musicList.get(position).getSongPic() != null) {
            mExecutorService.execute(new BlurRunnable());
        }
        //黑唱片转盘动画
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.my_anim);
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        mAnimation.setInterpolator(linearInterpolator);
        mMusicImage.startAnimation(mAnimation);
    }


    @Override
    public void onClick(View view) {
        int songListClickCount = 0;
        switch (view.getId()) {
            //播放模式
            case R.id.play_model:
                setPlayModePic();
                break;
            //上一曲
            case R.id.last_song:
                last();
                break;
            //播放或暂停
            case R.id.play_pause:
                playOrPause();

                break;

            //下一曲
            case R.id.next_song:
                next();

                break;
            //音乐播放列表
            case R.id.song_list:
                showOrDismissMusicList(songListClickCount);
                break;

            //歌曲下载
            case R.id.download:
                downloadMusic();
                break;
            //下载歌曲列表
            case R.id.download_list:
                showDownloadMusics();

                break;
            default:
                break;
        }
    }

    /**
     * 显示下载歌曲列表
     */
    private void showDownloadMusics() {
        View downLoadMusicListPopup = LayoutInflater.from(PlayActivity.this).inflate(R.layout.download_musiclist_popupwindow, null);
        mDownLoadListPopup = (ListView) downLoadMusicListPopup.findViewById(R.id.download_musiclist);
        mDownLoadMusicAdapter = new DownLoadMusicAdapter(PlayActivity.this, R.layout.download_music_item, mDownLoadMusicList);
        mDownLoadListPopup.setAdapter(mDownLoadMusicAdapter);
        //mDownLoadMusicAdapter.updateView(0,mDownLoadListPopup,mDownloadProgress+"%");
        PopupWindow popupWindow = new PopupWindow(downLoadMusicListPopup, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        ColorDrawable colorDrawable = new ColorDrawable();
        popupWindow.setBackgroundDrawable(colorDrawable);
        popupWindow.showAsDropDown(downloadList, 0, 0);
        popupWindow.setOutsideTouchable(true);
    }

    /**
     * 下载音乐
     */
    private void downloadMusic() {
        DLManager dlManager = DLManager.getInstance(PlayActivity.this);
        dlManager.setDebugEnable(true);
        String songLink = musicList.get(mPosition).getSongLink();
        String fileType = null;
        if (songLink.toLowerCase().contains("mp3")) {
            fileType = ".mp3";
        } else if (songLink.toLowerCase().contains("m4a")) {
            fileType = ".m4a";
        } else if (songLink.toLowerCase().contains("wma")) {
            fileType = ".wma";
        } else if (songLink.toLowerCase().contains("mp4")) {
            fileType = ".mp4";
        } else if (songLink.toLowerCase().contains("acc")) {
            fileType = ".acc";
        } else if (songLink.toLowerCase().contains("flac")) {
            fileType = ".flac";
        }
        dlManager.dlStart(musicList.get(mPosition).getSongLink(), Environment.getExternalStorageDirectory().getAbsolutePath(), musicList.get(mPosition).getSongTitle() + fileType, new SimpleDListener() {
            @Override
            public void onPrepare() {
                mProgressMap.put(musicList.get(mPosition).songTitle, 0);
                super.onPrepare();
            }

            @Override
            public void onStart(String fileName, String realUrl, int fileLength) {
                mProgressMap.put(musicList.get(mPosition).songTitle, 0);
                ToastUtil.showToast(PlayActivity.this, "下载开始");
                mFileSize = fileLength;

            }

            @Override
            public void onProgress(final int progress, String fileName) {
                mDownloadProgress = mFileSize > 0 ? (int) (progress * 100.0 / mFileSize) : 0;
                mDownloadProgress = mDownloadProgress > 100 ? 100 : mDownloadProgress;
                mProgressMap.put(fileName.split("\\.")[0], mDownloadProgress);
                if (mDownLoadMusicAdapter != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < mDownLoadMusicAdapter.getCount(); i++) {
                                String musicName = mDownLoadMusicAdapter.getItem(i).musicName;
                                int progressFinal;
                                if (mProgressMap != null) {
                                    progressFinal = mProgressMap.get(musicName);
                                } else {
                                    progressFinal = 0;
                                }

                                mDownLoadMusicAdapter.updateView(i, mDownLoadListPopup, progressFinal + "%");
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
/*                dlManager.dlStart(musicList.get(mPosition).getSongLink(), Environment.getExternalStorageDirectory().getAbsolutePath(), musicList.get(mPosition).getSongTitle() + fileType, new IDListener() {
                    @Override
                    public void onPrepare() {
                        ToastUtil.showToast(PlayActivity.this, "准备下载");

                    }

                    @Override
                    public void onStart(String fileName, String realUrl, int fileLength) {
                        ToastUtil.showToast(PlayActivity.this, "下载开始");
                        mFileSize = fileLength;

                    }

                    @Override
                    public void onProgress(int progress) {
                        mDownloadProgress = mFileSize > 0 ? progress * 100 / mFileSize : 0;
                       *//* mDownLoadMusicAdapter = (DownLoadMusicAdapter) mDownLoadListPopup.getAdapter();
                        mDownLoadMusicAdapter.clear();
                        mDownLoadMusicList.add(new DownLoadMusic(musicList.get(mPosition).getSongTitle(), mDownloadProgress + "%"));
                        mDownLoadMusicAdapter.addAll(mDownLoadMusicList);
                        mDownLoadMusicAdapter.notifyDataSetChanged();*//*

                     *//*  new Thread(new Runnable() {
                           @Override
                           public void run() {
                               runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       mDownLoadMusicAdapter = (DownLoadMusicAdapter) mDownLoadListPopup.getAdapter();
                                       mDownLoadMusicAdapter.clear();
                                       mDownLoadMusicList.add(new DownLoadMusic(musicList.get(mPosition).getSongTitle(), mDownloadProgress + "%"));
                                       mDownLoadMusicAdapter.addAll(mDownLoadMusicList);
                                       mDownLoadListPopup.setAdapter(mDownLoadMusicAdapter);
                                       mDownLoadMusicAdapter.notifyDataSetChanged();
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
        //    if (mDownLoadMusicList.size() > 0 && mDownLoadMusicList.contains())
        if (!mDownLoadMusicName.contains(musicList.get(mPosition).getSongTitle())) {
            mDownLoadMusicName.add(musicList.get(mPosition).getSongTitle());
            mDownLoadMusicList.add(new DownLoadMusic(musicList.get(mPosition).getSongTitle(), mDownloadProgress + "%"));
        }
    }

    /**
     * 显示或消失歌曲列表
     *
     * @param mSongListClickCount
     */
    private void showOrDismissMusicList(int mSongListClickCount) {
        mSongListClickCount++;
        if (mSongListClickCount % 2 == 0) {
            if (mPopupWindow != null) {
                mPopupWindow.dismiss();
            }
        } else {
            View musicListPopup = LayoutInflater.from(PlayActivity.this).inflate(R.layout.music_list_popupwindow, null);
            mMusicListView = (ListView) musicListPopup.findViewById(R.id.music_list);
            MusicListAdapter adapter = new MusicListAdapter(PlayActivity.this, R.layout.music_list_item, musicList);
            mMusicListView.setAdapter(adapter);
            mMusicListView.setOnItemClickListener(this);
            mPopupWindow = new PopupWindow(musicListPopup, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            ColorDrawable colorDrawable = new ColorDrawable();
            mPopupWindow.setBackgroundDrawable(colorDrawable);
            mPopupWindow.showAsDropDown(mMusicName, 0, 0);
            mPopupWindow.setOutsideTouchable(true);
        }
    }

    /**
     * 开始播放或暂停
     */
    private void playOrPause() {
        if (mIsPlaying) {
            pause();
            mMusicImage.clearAnimation();
            playAndPause.setSelected(true);
            mIsPlaying = false;

        } else {
            play();
            playAndPause.setSelected(false);
            mMusicImage.startAnimation(mAnimation);
            mIsPlaying = true;

        }
    }

    /**
     * 设置播放模式的图片
     */
    private void setPlayModePic() {
        mClickCount++;
        switch (mClickCount % 3) {
            //随机播放
            case 0:
                ToastUtil.showToast(this, "随机播放");
                playModel.setBackground(getResources().getDrawable(R.drawable.random));
                break;
            //单曲循环
            case 1:
                ToastUtil.showToast(this, "单曲循环");
                playModel.setBackground(getResources().getDrawable(R.drawable.single_recycle));
                break;
            //列表循环
            case 2:
                ToastUtil.showToast(this, "列表循环");
                playModel.setBackground(getResources().getDrawable(R.drawable.list_recycle));
                break;
            default:
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    /**
     * 歌曲列表單點擊操作
     *
     * @param parent
     * @param view
     * @param positionofMethod
     * @param id
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int positionofMethod, long id) {
        mPopupWindow.dismiss();

        initPlayerView(positionofMethod);
        playWithPosition(positionofMethod);
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


    class PlayMusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Config.GET_MUSIC_TIME.equals(intent.getAction())) {
                if (intent.getIntExtra("musicLength", 0) != 0) {
                    time = intent.getIntExtra("musicLength", 0);
                    mMusicTime.setText(getFormatTimeString(time));
                }
                if (intent.getIntExtra("seekBarPosition", 0) != 0) {
                    int seekBarposition = intent.getIntExtra("seekBarPosition", 0);
                    mPlayedMusicTime.setText(getFormatTimeString(seekBarposition));
                    mSeekBar.setProgress((int) (mSeekBar.getMax() * seekBarposition / time));

                }
            } else if (Config.GET_MUSIC_POSI.equals(intent.getAction())) {

                mPosition = intent.getIntExtra("mPosition", 0);
                //初始化播放器界面
                initPlayerView(mPosition);

            }


        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
    }


    /**
     * 高斯模糊任务线程
     */
    class BlurRunnable implements Runnable {

        @Override
        public void run() {
            final Bitmap bitmap = FastBlurUtil.GetUrlBitmap(musicList.get(mPosition).getSongPic(), 3);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBackGround.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    mBackGround.setImageBitmap(bitmap);
                }
            });
        }
    }

}
