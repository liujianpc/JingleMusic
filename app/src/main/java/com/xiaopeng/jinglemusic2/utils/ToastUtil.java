package com.xiaopeng.jinglemusic2.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by liujian on 2017/8/19.
 */

public class ToastUtil {
    private static Toast mToast;
    public static void showToast(Context context, String content){
        if (mToast != null){
            mToast.setText(content);
        }else {
            mToast = Toast.makeText(context,content,Toast.LENGTH_SHORT);
        }
        mToast.show();
    }
}
