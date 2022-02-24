package com.zero.aiweather.contract;

import android.app.Activity;

import com.google.gson.Gson;
import com.zero.aiweather.model.response.CityResponse;
import com.zero.base.net.NetApi;
import com.zero.base.util.Constant;
import com.zero.base.util.LogUtils;
import com.zero.base.util.OkHttpUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LocationPresenter implements LocationContract.IPresenter {
    private LocationContract.IView view;
    private Activity activity;

    public LocationPresenter(LocationContract.IView view, Activity activity) {
        this.view = view;
        this.activity = activity;
    }

    @Override
    public void getLocationId(String cityName) {
        String url = NetApi.chinaCities + "?location=" + cityName + "&key=" + Constant.ApiKey;
        OkHttpUtils.getRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                LogUtils.e(Constant.TAG, "数据获取失败！" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                CityResponse cityResponse = new Gson().fromJson(response.body().string(), CityResponse.class);
                if (cityResponse != null && cityResponse.getCode().equals("200")) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.getCityNameList(cityResponse);
                        }
                    });
                }
            }
        });
    }
}
