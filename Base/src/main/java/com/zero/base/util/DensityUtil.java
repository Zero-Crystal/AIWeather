package com.zero.base.util;

import android.content.Context;

import com.zero.base.AIWeatherApplication;

/**
 * Created by xiaoC on 2019/3/15.
 */

public class DensityUtil {
    /**
     *      * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *      *
     *      * @param context
     *      * @param dpValue
     *      * @return
     *     
     */
    public static int dip2px(float dpValue) {
        final float scale = AIWeatherApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     *      * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *      *
     *      * @param context
     *      * @param pxValue
     *      * @return
     *     
     */
    public static int px2dip(float pxValue) {
        final float scale = AIWeatherApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static float sp2px(float spValue) {
        float fontScale = AIWeatherApplication.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (spValue * fontScale + 0.5f);
    }

    public static float px2sp(float pxValue) {
        float fontScale = AIWeatherApplication.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (pxValue / fontScale + 0.5f);
    }

}