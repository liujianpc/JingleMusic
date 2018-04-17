package com.xiaopeng.jinglemusic2.ui;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Date: 2018/4/4
 * Created by LiuJian
 */

public class App extends Application {
    private static App instance;

    /**
     * 单例获取context
     *
     * @return
     */
    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
