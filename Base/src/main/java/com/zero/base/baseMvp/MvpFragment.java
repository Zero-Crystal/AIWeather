package com.zero.base.baseMvp;

import android.os.Bundle;

import com.zero.base.base.BaseFragment;
import com.zero.base.base.BasePresenter;
import com.zero.base.base.IBaseView;

/**
 * 网络请求Fragment基类
 * */
public abstract class MvpFragment<P extends BasePresenter> extends BaseFragment {
    public P mPresenter;

    /**
     * 创建Presenter
     * */
    protected abstract P createPresenter();

    @Override
    public void initBeforeView(Bundle saveInstanceState) {
        mPresenter = createPresenter();
        mPresenter.attach((IBaseView)this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPresenter.detach((IBaseView)this);
    }
}
