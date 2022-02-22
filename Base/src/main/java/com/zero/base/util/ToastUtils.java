package com.zero.base.util;

import android.widget.Toast;

import com.zero.base.AIWeatherApplication;

public class ToastUtils {

    public static void toastShort(String message) {
        Toast.makeText(AIWeatherApplication.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void toastLong(String message) {
        Toast.makeText(AIWeatherApplication.getContext(), message, Toast.LENGTH_LONG).show();
    }
}
