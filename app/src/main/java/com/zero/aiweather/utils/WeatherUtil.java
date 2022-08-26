package com.zero.aiweather.utils;

import androidx.core.content.ContextCompat;

import com.zero.aiweather.R;
import com.zero.aiweather.application.MyApplication;

public class WeatherUtil {

    public static int getWeatherIcon(String text) {
        int resourceId = 0;
        switch (text) {
            case "晴":
                resourceId = R.mipmap.weather_sunny;
                break;
            case "多云":
                resourceId = R.mipmap.weather_duoyun;
                break;
            case "多云转晴":
                resourceId = R.mipmap.weather_qingzhuanduoyun;
                break;
            case "阴":
                resourceId = R.mipmap.weather_yintian;
                break;
            case "霾":
                resourceId = R.mipmap.weather_mai;
                break;
            case "雾":
                resourceId = R.mipmap.weather_wu;
                break;
            case "小雨":
            case "中雨":
                resourceId = R.mipmap.weather_xiaoyu;
                break;
            case "大雨":
            case "暴雨":
                resourceId = R.mipmap.weather_rain;
                break;
            case "雨夹雪":
                resourceId = R.mipmap.weather_sleet;
                break;
            case "小雪":
            case "中雪":
                resourceId = R.mipmap.weather_xiaoxue;
                break;
            case "大雪":
            case "暴雪":
                resourceId = R.mipmap.weather_daxue;
                break;
            default:
                resourceId = R.mipmap.weather_duoyunzhuanqing;
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
                color = ContextCompat.getColor(MyApplication.getContext(), R.color.green);
                break;
            case 2:
                color = ContextCompat.getColor(MyApplication.getContext(), R.color.yellow);
                break;
            case 3:
                color = ContextCompat.getColor(MyApplication.getContext(), R.color.orange);
                break;
            case 4:
                color = ContextCompat.getColor(MyApplication.getContext(), R.color.red);
                break;
            case 5:
                color = ContextCompat.getColor(MyApplication.getContext(), R.color.purple);
                break;
            case 6:
                color = ContextCompat.getColor(MyApplication.getContext(), R.color.maroon);
                break;
            default:
                color = ContextCompat.getColor(MyApplication.getContext(), R.color.white_90);
                break;
        }
        return color;
    }

    /**
     * 获取生活指数iconId
     * */
    public static int getAdviceIcon(String level) {
        int iconId = 0;
        switch (level) {
            case "1":
                iconId = R.drawable.ic_sports;
                break;
            case "3":
                iconId = R.drawable.ic_clothes;
                break;
            case "6":
                iconId = R.drawable.ic_travel_;
                break;
            case "9":
                iconId = R.drawable.ic_medical;
                break;
            case "13":
                iconId = R.drawable.ic_beauty;
                break;
            case "16":
                iconId = R.drawable.ic_suncream;
                break;
            default:
                iconId = R.drawable.icon_101;
                break;
        }
        return iconId;
    }

    public static int getMapIcon(String title) {
        int iconId = 0;
        switch (title) {
            case "体感温度":
                iconId = R.drawable.ic_temperature;
                break;
            case "降水量":
                iconId = R.drawable.ic_rain;
                break;
            case "湿度":
                iconId = R.drawable.ic_humidity;
                break;
            case "大气压强":
                iconId = R.drawable.ic_pressure;
                break;
            case "能见度":
                iconId = R.drawable.ic_invisible;
                break;
            case "云量":
                iconId = R.drawable.ic_cloud;
                break;
        }
        return iconId;
    }

}
