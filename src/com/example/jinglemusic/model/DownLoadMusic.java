package com.example.jinglemusic.model;

/**
 * Created by liujian on 2017/8/28.
 */

public class DownLoadMusic {
    public String musicName;
    public String downLoadProgress;
    public DownLoadMusic(String musicName, String downLoadProgress){
        this.musicName = musicName;
        this.downLoadProgress = downLoadProgress;
    }
}
