/**
 *
 */
package com.example.jinglemusic;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * @author FBJH73
 */
public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        Log.d("BaseActivity", getClass().getSimpleName());
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}

	