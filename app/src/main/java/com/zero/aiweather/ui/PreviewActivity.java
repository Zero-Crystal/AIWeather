package com.zero.aiweather.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.zero.aiweather.R;
import com.zero.base.util.ToastUtils;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

public class PreviewActivity extends AppCompatActivity {
    private List<String> permissionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        chexkPermission();
    }

    private void chexkPermission() {
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
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            for (String permission : permissions) {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        } else {
            startMainActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                for (int i : grantResults) {
                    if (i != PackageManager.PERMISSION_GRANTED) {
                        ToastUtils.toastShort(PreviewActivity.this, "授权失败，无法使用此app");
                        finish();
                    }
                }
                startMainActivity();
                break;
            default:
                break;
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(PreviewActivity.this, MainActivity.class);
        startActivity(intent);
    }
}