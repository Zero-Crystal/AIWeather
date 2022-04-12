package com.zero.base.baseMvpNet;

import android.os.Bundle;

import com.zero.base.base.BaseActivity;
import com.zero.base.base.BasePresenter;
import com.zero.base.base.IBaseView;

/**
 * 网络请求Activity基类
 * */
public abstract class MvpActivity<P extends BasePresenter> extends BaseActivity {
    protected P mPresenter;

    /**
     * 创建Presenter
     * */
    protected abstract P createPresenter();

    @Override
    public void initBeforeView(Bundle saveInstanceState) {
        mPresenter = createPresenter();
        mPresenter.attach((IBaseView) this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detach((IBaseView) this);
    }
}
