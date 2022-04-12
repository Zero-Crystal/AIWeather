package com.zero.aiweather.utils;

import android.widget.Toast;

import com.zero.base.BaseApplication;

public class ToastUtil {

    public static void toastShort(String message) {
        Toast.makeText(BaseApplication.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void toastLong(String message) {
        Toast.makeText(BaseApplication.getContext(), message, Toast.LENGTH_LONG).show();
    }
}
