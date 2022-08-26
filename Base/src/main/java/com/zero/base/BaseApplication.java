package com.zero.base;

import android.content.Context;

import com.zero.base.util.ActivityManager;

import org.litepal.LitePalApplication;

/**
 * 工程管理类
 * */
public class BaseApplication extends LitePalApplication {
    private static Context context;
    private static ActivityManager activityManager;
    private static BaseApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        context = getApplicationContext();
        activityManager = new ActivityManager();
    }

    public static Context getContext() {
        return context;
    }

    public static ActivityManager getActivityManager() {
        return activityManager;
    }

    public static BaseApplication getApplication() {
        return application;
    }
}
