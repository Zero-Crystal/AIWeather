package com.zero.aiweather.contract;

import android.app.Activity;

import com.google.gson.Gson;
import com.zero.aiweather.model.response.Day7Response;
import com.zero.aiweather.model.response.Hour24Response;
import com.zero.aiweather.model.response.NowResponse;
import com.zero.base.net.NetApi;
import com.zero.base.util.Constant;
import com.zero.base.util.LogUtils;
import com.zero.base.util.OkHttpUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherPresenter implements WeatherContract.IPresenter {
    private WeatherContract.IView view;
    private Activity activity;

    public WeatherPresenter(WeatherContract.IView view, Activity activity) {
        this.view = view;
        this.activity = activity;
    }

    @Override
    public void getWeatherNow(String location) {
        String url = NetApi.BaseUrl + NetApi.weatherNow + "?location=" + location + "&key=" + Constant.ApiKey;
        OkHttpUtils.getRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                LogUtils.e(Constant.TAG, "数据请求失败！" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                NowResponse nowResponse = new Gson().fromJson(response.body().string(), NowResponse.class);
                if (nowResponse != null && nowResponse.getCode().equals("200")) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.showWeatherNow(nowResponse);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void getWeather24Hour(String location) {
        String url = NetApi.BaseUrl + NetApi.weather24Hour + "?location=" + location + "&key=" + Constant.ApiKey;
        OkHttpUtils.getRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                LogUtils.e(Constant.TAG, "数据请求失败！" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Hour24Response hourResponse = new Gson().fromJson(response.body().string(), Hour24Response.class);
                if (hourResponse != null && hourResponse.getCode().equals("200")) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.showWeather24Hour(hourResponse);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void getWeather7Day(String location) {
        String url = NetApi.BaseUrl + NetApi.weather7Day + "?location=" + location + "&key=" + Constant.ApiKey;
        OkHttpUtils.getRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                LogUtils.e(Constant.TAG, "数据请求失败！" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Day7Response dayResponse = new Gson().fromJson(response.body().string(), Day7Response.class);
                if (dayResponse != null && dayResponse.getCode().equals("200")) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.showWeather7Day(dayResponse);
                        }
                    });
                }
            }
        });
    }
}
