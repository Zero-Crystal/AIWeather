package com.zero.base.util;

import com.zero.base.R;

public class WeatherUtils {

    public static int getWeatherIcon(String text) {
        int resourceId = 0;
        switch (text) {
            case "晴":
                resourceId = R.drawable.sunny;
                break;
            case "多云":
                resourceId = R.drawable.duoyun;
                break;
            case "多云转晴":
                resourceId = R.drawable.qingzhuanduoyun;
                break;
            case "阴":
                resourceId = R.drawable.yintian;
                break;
            case "霾":
                resourceId = R.drawable.mai;
                break;
            case "雾":
                resourceId = R.drawable.wu;
                break;
            case "小雨":
            case "中雨":
                resourceId = R.drawable.xiaoyu;
                break;
            case "大雨":
            case "暴雨":
                resourceId = R.drawable.rain;
                break;
            case "雨夹雪":
                resourceId = R.drawable.sleet;
                break;
            case "小雪":
            case "中雪":
                resourceId = R.drawable.xiaoxue;
                break;
            case "大雪":
            case "暴雪":
                resourceId = R.drawable.daxue;
                break;
            default:
                resourceId = R.drawable.duoyunzhuanqing;
                break;
        }
        return resourceId;
    }
}
