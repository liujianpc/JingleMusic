package com.xiaopeng.jinglemusic2;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author liujian
 * @date 2017/8/1
 */

public class Music implements Parcelable {

    @SerializedName(value = "title", alternate = {"songName","name"})
    public String songTitle;

    @SerializedName(value = "author", alternate = {"artistName",})
    public String author;

    @SerializedName(value = "url", alternate = {"songLink","source"})
    public String songLink;

    @SerializedName(value = "pic", alternate = {"songPicRadio","pic_200"})
    public String songPic;

    @SerializedName(value = "lrc", alternate = {"lrcLink"})
    String songLrc;

    public Music(String songTitle, String author, String songLink, String songPic, String songLrc) {
        this.songTitle = songTitle;
        this.author = author;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }

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

    public static Creator<Music> getCREATOR() {
        return CREATOR;
    }

    protected Music(Parcel in) {
        songTitle = in.readString();
        author = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(songTitle);
        dest.writeString(author);
        dest.writeString(songLink);
        dest.writeString(songPic);
        dest.writeString(songLrc);
    }
}
