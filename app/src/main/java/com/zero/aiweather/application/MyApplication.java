package com.zero.aiweather.application;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.zero.base.BaseApplication;
import com.zero.base.baseNewNet.NetWorkApi;
import com.zero.base.util.KLog;

import org.litepal.LitePal;

public class MyApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化日志输出等级
        KLog.LEVEL = KLog.D;
        //初始化
        NetWorkApi.init(new NetworkRequiredInfo(this));
        //百度地图
        SDKInitializer.setAgreePrivacy(getApplicationContext(),true);
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);
        LocationClient.setAgreePrivacy(true);
        //LitePal
        LitePal.initialize(this);
    }
}
