package com.zero.aiweather.application;

import android.app.Application;

import com.zero.base.BaseApplication;
import com.zero.base.baseNewNet.NetWorkApi;

public class MyApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化
        NetWorkApi.init(new NetworkRequiredInfo(this));
    }
}
