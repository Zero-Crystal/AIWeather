package com.zero.aiweather.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.zero.aiweather.R;

import java.util.logging.Handler;

public class PopupWindowUtil {
    private PopupWindow mPopupWindow;
    private Context context;

    public PopupWindowUtil(Context context) {
        this.context = context;
    }

    public void setAutoTopPopupWindow(View view) {
        mPopupWindow = new PopupWindow();
        mPopupWindow.setContentView(view);
        mPopupWindow.setWidth(DensityUtil.dp2px(120));
        mPopupWindow.setHeight(DensityUtil.dp2px(180));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
    }

    /**
     * 右侧，宽高自适应
     * */
    public void showAutoRightTop(View view, int offsetX, int offsetY) {
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setContentView(view);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setAnimationStyle(R.style.AnimationRightFade);
        mPopupWindow.showAtLocation(view, Gravity.RIGHT|Gravity.TOP, offsetX, offsetY);
    }

    /**
     * 右侧，高度自适应
     * */
    public void showRightTop(View view) {
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        mPopupWindow = new PopupWindow(view, display.getWidth() / 3 * 2, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setContentView(view);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setAnimationStyle(R.style.AnimationRightFade);
        mPopupWindow.showAtLocation(view, Gravity.RIGHT|Gravity.TOP, 0, 279);
    }

    /**
     * 底部
     * */
    public void showBottom(View view, PopupWindow.OnDismissListener onDismissListener) {
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setContentView(view);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setAnimationStyle(R.style.AnimationBottomFade);
        mPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        mPopupWindow.setOnDismissListener(onDismissListener);
    }

    /**
     * 关闭PopupWindowCities
     * */
    public void closePopupWindow() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    public void showAsViewUp(View view, int offset) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0] - offset;
        int y = location[1] - view.getHeight() - offset;
        mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, y);
    }
}
