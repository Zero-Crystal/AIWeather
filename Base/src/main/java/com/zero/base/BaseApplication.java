package com.zero.base;

import android.app.Application;
import android.content.Context;

import com.zero.base.util.ActivityManager;
import com.zero.base.util.LogUtils;

/**
 * 工程管理类
 * */
public class BaseApplication extends Application {
    private static Context context;
    private static ActivityManager activityManager;
    private static BaseApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        context = getApplicationContext();
        activityManager = new ActivityManager();
        //初始化日志输出等级
        LogUtils.LEVEL = LogUtils.DEBUG;
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
