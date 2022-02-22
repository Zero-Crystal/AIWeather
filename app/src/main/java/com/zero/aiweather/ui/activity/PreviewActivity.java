package com.zero.aiweather.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.zero.aiweather.R;
import com.zero.base.base.BaseActivity;
import com.zero.base.controller.ActivityController;
import com.zero.base.util.Constant;
import com.zero.base.util.LogUtils;
import com.zero.base.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class PreviewActivity extends BaseActivity {
    private List<String> permissionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        checkPermission();
    }

    private void checkPermission() {
        LogUtils.d(Constant.TAG, "PreviewActivity: ----------------> checkPermission");
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add("android.permission.READ_EXTERNAL_STORAGE");
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add("android.permission.WRITE_EXTERNAL_STORAGE");
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add("android.permission.ACCESS_COARSE_LOCATION");
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add("android.permission.ACCESS_FINE_LOCATION");
        }
        if (permissionList.size() > 0) {
            LogUtils.d(Constant.TAG, "PreviewActivity: ----------------> checkPermission: permissionList size=" + permissionList.size());
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
        } else {
            LogUtils.d(Constant.TAG, "PreviewActivity: ----------------> checkPermission: start Main");
            MainActivity.startMain(PreviewActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogUtils.d(Constant.TAG, "PreviewActivity: ----------------> onRequestPermissionsResult: request code=" + requestCode);
        switch (requestCode) {
            case 1:
                for (int i : grantResults) {
                    if (i != PackageManager.PERMISSION_GRANTED) {
                        ToastUtils.toastShort("授权失败，无法使用此app");
                        LogUtils.d(Constant.TAG, "PreviewActivity: ----------------> onRequestPermissionsResult: 授权失败");
                    }
                }
                MainActivity.startMain(PreviewActivity.this);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.d(Constant.TAG, "PreviewActivity: ----------------> onPause");
        ActivityController.finishAll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d(Constant.TAG, "PreviewActivity: ----------------> onDestroy");
    }
}