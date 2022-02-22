package com.zero.aiweather.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zero.aiweather.databinding.ActivityMainBinding;
import com.zero.base.base.BaseActivity;
import com.zero.base.util.Constant;
import com.zero.base.util.LogUtils;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.tbTitle);
        LogUtils.d(Constant.TAG, "MainActivity: ----------------> onCreate");
    }

    public static void startMain(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}