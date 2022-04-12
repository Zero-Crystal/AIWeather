package com.zero.base.base;

import java.lang.ref.WeakReference;

/**
 * Presenter基类
 * */
public class BasePresenter<V extends IBaseView> {
    private WeakReference<V> mWeakReference;

    /**
     * 关联view
     * @param v
     */
    public void attach(V v) {
        mWeakReference = new WeakReference<V>(v);
    }

    /**
     * 分离View
     * @param v
     * */
    public void detach(V v) {
        if (mWeakReference != null) {
            mWeakReference.clear();
            mWeakReference = null;
        }
    }

    /**
     * 获取View
     * */
    public V getView() {
        if (mWeakReference != null) {
            return mWeakReference.get();
        }
        return null;
    }

}
