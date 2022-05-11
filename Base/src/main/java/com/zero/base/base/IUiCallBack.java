package com.zero.base.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * UI回调接口
 * */
public interface IUiCallBack {
    //初始化saveInstanceState
    void initBeforeView(Bundle saveInstanceState);
    //初始化数据
    void initDate(Bundle saveInstanceState);
    //获取布局
    View getLayoutView();
}
