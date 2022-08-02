package com.zero.aiweather.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.zero.aiweather.R;

public class PopupWindowUtil {
    private PopupWindow mPopupWindow;
    private Context context;

    public PopupWindowUtil(Context context) {
        this.context = context;
    }

    /**
     * view-顶部
     * */
    public void showTopPopupWindow(View view) {
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setContentView(view);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setAnimationStyle(R.style.AnimationBottomFade);
        mPopupWindow.showAsDropDown(view, 0, 0);
    }

    /**
     * 右侧，高度自适应
     * */
    public void showRightPopupWindow(View view) {
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
    public void showBottomPopupWindow(View view, PopupWindow.OnDismissListener onDismissListener) {
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
}
