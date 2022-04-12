package com.zero.base.base;

import android.os.Bundle;
import android.view.View;

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
