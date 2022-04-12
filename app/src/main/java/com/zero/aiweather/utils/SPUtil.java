package com.zero.aiweather.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.zero.base.BaseApplication;

public class SPUtil {
    private static final String NAME="config";

    public static void putBoolean(String key, boolean value) {
        SharedPreferences sp = BaseApplication.getContext().getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        SharedPreferences sp = BaseApplication.getContext().getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    public static void putString(String key, String value) {
        SharedPreferences sp = BaseApplication.getContext().getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    public static String getString(String key, String defValue) {
        if(BaseApplication.getContext() != null){
            SharedPreferences sp = BaseApplication.getContext().getSharedPreferences(NAME,
                    Context.MODE_PRIVATE);
            return sp.getString(key, defValue);
        }
        return "";
    }

    public static void putInt(String key, int value) {
        SharedPreferences sp = BaseApplication.getContext().getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }


    public static int getInt(String key, int defValue) {
        SharedPreferences sp = BaseApplication.getContext().getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

    public static void remove(String key) {
        SharedPreferences sp = BaseApplication.getContext().getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        sp.edit().remove(key).commit();
    }
}
