package com.example.jinglemusic;

import java.io.Serializable;

/**
 * Created by liujian on 2017/8/1.
 */

public class Music implements Serializable {
    String songTitle;
    String songLink;
    String songPic;

    public String getSongPic() {
        return songPic;
    }

    public void setSongPic(String songPic) {
        this.songPic = songPic;
    }

    public String getSongLrc() {
        return songLrc;
    }

    public void setSongLrc(String songLrc) {
        this.songLrc = songLrc;
    }

    String songLrc;

    public Music(String songTitle, String songLink,String songPic, String songLrc) {
        this.songTitle = songTitle;
        this.songLink = songLink;
        this.songPic = songPic;
        this.songLrc = songLrc;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }
}
