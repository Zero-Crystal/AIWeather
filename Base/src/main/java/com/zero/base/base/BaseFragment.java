package com.zero.base.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zero.base.R;

/**
 * Fragment基类
 * */
public abstract class BaseFragment extends Fragment implements IUiCallBack {
    protected Activity context;
    private LayoutInflater inflater;
    //弹窗
    private Dialog loading;

    @Override
    public void initBeforeView(Bundle saveInstanceState) {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.context = (Activity) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBeforeView(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        return getLayoutView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initDate(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
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

    public LayoutInflater getInflater() {
        return inflater;
    }
}
