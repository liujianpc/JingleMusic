// IMyAidlInterface.aidl
package com.xiaopeng.jinglemusic2;

// Declare any non-default types here with import statements
import com.xiaopeng.jinglemusic2.Music;

interface IPlayServiceInterface {

    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
/*   void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);*/
      //首次播放
      void firstPlay(int position);
      //播放
      void play();
      //暂停
      void pause();
      //停止播放
      void stop();
      //上一首
      void last();
      //下一首
      void next();
      //播放模式
      void playMode(int mode);
      //帶有位置的播放
      void playWithPosition(int position);

}
