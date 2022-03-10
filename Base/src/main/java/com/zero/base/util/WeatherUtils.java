package com.zero.base.util;

import androidx.core.content.ContextCompat;

import com.zero.base.AIWeatherApplication;
import com.zero.base.R;

public class WeatherUtils {

    public static int getWeatherIcon(String text) {
        int resourceId = 0;
        switch (text) {
            case "晴":
                resourceId = R.drawable.weather_sunny;
                break;
            case "多云":
                resourceId = R.drawable.weather_duoyun;
                break;
            case "多云转晴":
                resourceId = R.drawable.weather_qingzhuanduoyun;
                break;
            case "阴":
                resourceId = R.drawable.weather_yintian;
                break;
            case "霾":
                resourceId = R.drawable.weather_mai;
                break;
            case "雾":
                resourceId = R.drawable.weather_wu;
                break;
            case "小雨":
            case "中雨":
                resourceId = R.drawable.weather_xiaoyu;
                break;
            case "大雨":
            case "暴雨":
                resourceId = R.drawable.weather_rain;
                break;
            case "雨夹雪":
                resourceId = R.drawable.weather_sleet;
                break;
            case "小雪":
            case "中雪":
                resourceId = R.drawable.weather_xiaoxue;
                break;
            case "大雪":
            case "暴雪":
                resourceId = R.drawable.weather_daxue;
                break;
            default:
                resourceId = R.drawable.weather_duoyunzhuanqing;
                break;
        }
        return resourceId;
    }

    /**
     * 获取空气质量等级对应颜色
     * */
    public static int getAirAqiColor(int level) {
        int color = 0;
        switch (level) {
            case 1:
                color = ContextCompat.getColor(AIWeatherApplication.getContext(), R.color.green);
                break;
            case 2:
                color = ContextCompat.getColor(AIWeatherApplication.getContext(), R.color.yellow);
                break;
            case 3:
                color = ContextCompat.getColor(AIWeatherApplication.getContext(), R.color.orange);
                break;
            case 4:
                color = ContextCompat.getColor(AIWeatherApplication.getContext(), R.color.red);
                break;
            case 5:
                color = ContextCompat.getColor(AIWeatherApplication.getContext(), R.color.purple);
                break;
            case 6:
                color = ContextCompat.getColor(AIWeatherApplication.getContext(), R.color.maroon);
                break;
            default:
                color = ContextCompat.getColor(AIWeatherApplication.getContext(), R.color.green);
                break;
        }
        return color;
    }
}
