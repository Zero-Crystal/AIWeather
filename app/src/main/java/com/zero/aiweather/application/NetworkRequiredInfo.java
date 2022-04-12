package com.zero.aiweather.application;

import android.app.Application;

import com.zero.aiweather.BuildConfig;
import com.zero.base.baseNewNet.INetworkRequiredInfo;

public class NetworkRequiredInfo implements INetworkRequiredInfo {

    private Application application;

    public NetworkRequiredInfo(Application application){
        this.application = application;
    }

    @Override
    public String getAppVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    @Override
    public String getAppVersionCode() {
        return String.valueOf(BuildConfig.VERSION_CODE);
    }

    @Override
    public boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    @Override
    public Application getApplicationContext() {
        return application;
    }
}
