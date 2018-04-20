package com.xiaopeng.jinglemusic2.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Date: 2018/4/20
 * Created by LiuJian
 */

public class MusicInfo {


    @SerializedName(value = "hash")
    private String hash;

    @SerializedName(value = "songname", alternate = {"SONGNAME","name"})
    private String songname;

    @SerializedName(value = "songid", alternate = {"MUSICRID","id"})
    private String songid;

    @SerializedName(value = "artistname", alternate = {"singername", "ARTIST","singerName"})
    private String artistname;

    @SerializedName(value = "info")
    private String info;

    public MusicInfo(String hash, String songname, String songid, String artistname, String info) {
        this.hash = hash;
        this.songname = songname;
        this.songid = songid;
        this.artistname = artistname;
        this.info = info;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getSongname() {
        return songname;
    }

    public void setSongname(String songname) {
        this.songname = songname;
    }

    public String getSongid() {
        return songid;
    }

    public void setSongid(String songid) {
        this.songid = songid;
    }

    public String getArtistname() {
        return artistname;
    }

    public void setArtistname(String artistname) {
        this.artistname = artistname;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
