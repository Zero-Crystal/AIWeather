package com.zero.aiweather.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zero.aiweather.R;
import com.zero.aiweather.application.MyApplication;
import com.zero.base.BaseApplication;

public class ToastUtil {

    private static Toast mToast;

    public static void showToast(int resId) {
        String text = MyApplication.getContext().getString(resId);
        showToast(text);
    }

    public static void showToast(String text){
        showToast(text, Gravity.BOTTOM);
    }

    public static void showToastCenter(String text){
        showToast(text, Gravity.CENTER);
    }

    public static void showToast(String text, int gravity){
        cancelToast();
        LayoutInflater inflater = (LayoutInflater) MyApplication.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast_layout, null);
        ((TextView) layout.findViewById(R.id.tv_toast_text)).setText(text);
        mToast = new Toast(MyApplication.getContext());
        mToast.setView(layout);
        mToast.setGravity(gravity, 0, 100);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }

    public static void cancelToast() {
        if (mToast != null){
            mToast.cancel();
        }
    }
}
