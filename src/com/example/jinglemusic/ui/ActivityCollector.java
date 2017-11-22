/**
 *
 */
package com.example.jinglemusic.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

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
