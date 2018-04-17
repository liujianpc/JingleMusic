/**
 *
 */
package com.xiaopeng.jinglemusic2.ui;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FBJH73
 */
public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<Activity>();

    public static void addActivity(Activity activity) {
        activities.add(activity);

    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAllActivity() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }

    }

}
