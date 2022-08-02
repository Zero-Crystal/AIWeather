package com.zero.aiweather.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zero.aiweather.application.MyApplication;
import com.zero.aiweather.databinding.ActivityPreviewBinding;
import com.zero.base.BaseApplication;
import com.zero.base.base.BaseActivity;
import com.zero.base.util.KLog;
import com.zero.aiweather.utils.ToastUtil;

public class PreviewActivity extends BaseActivity {
    private ActivityPreviewBinding binding;
    private RxPermissions rxPermissions;//权限请求框架
    private final String[] permissionGroup = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    public void initBeforeView(Bundle saveInstanceState) {

    }

    @Override
    public void initDate(Bundle saveInstanceState) {
        rxPermissions = new RxPermissions(this);
        checkPermission();
    }

    @Override
    public View getLayoutView() {
        binding = ActivityPreviewBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint("CheckResult")
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {//6.0 或 6.0以上
            rxPermissions.request(permissionGroup)
                    .subscribe(granted -> {
                        if (granted) {
                            KLog.d("checkPermission: 授权成功");
                            MyApplication.getActivityManager().removeActivity(PreviewActivity.this);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                    MainActivity.startMain(PreviewActivity.this);
                                }
                            }, 1000);
                        } else {
                            KLog.d("checkPermission: 授权失败");
                            ToastUtil.showToast("授权失败，无法使用此app");
                        }
                    });
        } else {
            ToastUtil.showToast("你的版本在Android6.0以下，不需要动态申请");
        }
    }
}