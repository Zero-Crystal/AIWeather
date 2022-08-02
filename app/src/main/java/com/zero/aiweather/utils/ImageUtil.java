package com.zero.aiweather.utils;

import android.graphics.drawable.Drawable;

import com.zero.aiweather.R;
import com.zero.aiweather.application.MyApplication;

public class ImageUtil {

    public static Drawable getBackgroundDrawable(int position) {
        Drawable bgDrawable;
        switch (position) {
            case 0:
                bgDrawable = MyApplication.getContext().getDrawable(R.drawable.bg_img1);
                break;
            case 1:
                bgDrawable = MyApplication.getContext().getDrawable(R.drawable.bg_img2);
                break;
            case 2:
                bgDrawable = MyApplication.getContext().getDrawable(R.drawable.bg_img3);
                break;
            case 3:
                bgDrawable = MyApplication.getContext().getDrawable(R.drawable.bg_img4);
                break;
            case 4:
                bgDrawable = MyApplication.getContext().getDrawable(R.drawable.bg_img5);
                break;
            case 5:
                bgDrawable = MyApplication.getContext().getDrawable(R.drawable.bg_img6);
                break;
            default:
                bgDrawable = MyApplication.getContext().getDrawable(R.drawable.bg_sun);
                break;
        }
        return bgDrawable;
    }
}
