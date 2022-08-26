package com.zero.base.base;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zero.base.BaseApplication;
import com.zero.base.R;

import java.util.Objects;

/**
 * Activity基类
 * */
public abstract class BaseActivity extends AppCompatActivity implements IUiCallBack {
    //Activity 上下文
    protected Activity context;
    //弹窗
    private Dialog loading;

    private static final int FAST_CLICK_DELAY_TIME = 2 * 1000;
    private static long lastClickTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBeforeView(savedInstanceState);
        context = this;
        BaseApplication.getActivityManager().addActivity(this);
        if (getLayoutView() != null) {
            //绑定视图
            setContentView(getLayoutView());
        }
        initDate(savedInstanceState);
    }

    /**
     * 弹窗出现
     */
    protected void showLoadingDialog() {
        if (loading == null) {
            loading = new Dialog(context, R.style.loading_dialog);
        }
        loading.setContentView(R.layout.dialog_loading);
        loading.setCancelable(false);
        loading.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        loading.show();
    }

    /**
     * 弹窗隐藏
     */
    protected void hideLoadingDialog() {
        if (loading != null) {
            loading.dismiss();
        }
        loading = null;
    }

    /**
     * 两次点击间隔是否少于 2 * 1000ms
     *
     * @return flag boolean
     */
    protected static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) > FAST_CLICK_DELAY_TIME) {
            lastClickTime = currentClickTime;
            flag = false;
        }
        return flag;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.getActivityManager().removeActivity(this);
    }
}
