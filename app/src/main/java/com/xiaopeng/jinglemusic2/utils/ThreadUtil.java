package com.xiaopeng.jinglemusic2.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Date: 2018/4/19
 * Created by LiuJian
 *
 * @author XP-PC-XXX
 */

public class ThreadUtil {

    private static final ExecutorService sThreadPool = Executors.newFixedThreadPool(5);

    public void executeThread(Runnable runnable) {
        execute(runnable, null, Process.THREAD_PRIORITY_BACKGROUND);
    }

    private void execute(final Runnable runnable, final Runnable callback, final int threadPriority) {
        if (!sThreadPool.isShutdown()) {
            Handler handler = null;
            if (callback != null) {
                handler = new Handler(Looper.myLooper());
            }
            final Handler finalHandler = handler;
            sThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    Process.setThreadPriority(threadPriority);
                    runnable.run();
                    if (callback != null && finalHandler != null) {
                        finalHandler.post(callback);
                    }
                }
            });
        }
    }
}
