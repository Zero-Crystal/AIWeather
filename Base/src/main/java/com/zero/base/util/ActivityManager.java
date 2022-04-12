package com.zero.base.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity 管理工具
 * @author zero
 * */
public class ActivityManager {
    public List<Activity> activityList = new ArrayList<>();

    public void addActivity(Activity activity) {
        if (activity != null) {
            activityList.add(activity);
        }
    }

    public void removeActivity(Activity activity) {
        if (activity != null) {
            activityList.remove(activity);
        }
    }

    public void finishAll() {
        for (Activity activity : activityList) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        activityList.clear();
    }

    public Activity getTaskTop() {
        return activityList.get(activityList.size() - 1);
    }
}
