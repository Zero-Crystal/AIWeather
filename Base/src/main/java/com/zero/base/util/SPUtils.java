package com.zero.base.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.zero.base.AIWeatherApplication;

public class SPUtils {
    private static final String LocationSP = "Location";

    public static void saveLaLongLocation(String key, String message) {
        SharedPreferences.Editor editor = AIWeatherApplication.getContext().getSharedPreferences(LocationSP, Context.MODE_PRIVATE).edit();
        editor.putString(key, message);
        editor.apply();
    }

    public static String getLaLongLocation(String key) {
        SharedPreferences sp = AIWeatherApplication.getContext().getSharedPreferences(LocationSP, Context.MODE_PRIVATE);
        String location = sp.getString(key, "101010100");
        return location;
    }

    public static void saveLocation(String key, String message) {
        SharedPreferences.Editor editor = AIWeatherApplication.getContext().getSharedPreferences(LocationSP, Context.MODE_PRIVATE).edit();
        editor.putString(key, message);
        editor.apply();
    }

    public static String getLocation(String key) {
        SharedPreferences sp = AIWeatherApplication.getContext().getSharedPreferences(LocationSP, Context.MODE_PRIVATE);
        String location = sp.getString(key, "北京");
        return location;
    }
}
