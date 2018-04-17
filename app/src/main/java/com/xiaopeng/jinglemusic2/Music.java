package com.xiaopeng.jinglemusic2;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by liujian on 2017/8/1.
 */

public class Music implements Parcelable {
    public String songTitle;
    public String songLink;
    public String songPic;

    protected Music(Parcel in) {
        songTitle = in.readString();
        songLink = in.readString();
        songPic = in.readString();
        songLrc = in.readString();
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

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

    public Music(String songTitle, String songLink, String songPic, String songLrc) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(songTitle);
        dest.writeString(songLink);
        dest.writeString(songPic);
        dest.writeString(songLrc);
    }
}
