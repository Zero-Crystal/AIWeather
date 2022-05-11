package com.zero.base.base;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zero.base.R;

import java.util.Objects;

/**
 * Fragment基类
 * */
public abstract class BaseFragment extends Fragment implements IUiCallBack {
    private Activity context;
    private LayoutInflater inflater;
    private ViewGroup container;
    //弹窗
    private Dialog mDialog;

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
        this.container = container;
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

    @Override
    public void initBeforeView(Bundle saveInstanceState) {

    }

    /**
     * 弹窗出现
     */
    protected void showLoadingDialog() {
        if (mDialog == null) {
            mDialog = new Dialog(context, R.style.loading_dialog);
        }
        mDialog.setContentView(R.layout.dialog_loading);
        mDialog.setCancelable(false);
        Objects.requireNonNull(mDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.show();
    }

    /**
     * 弹窗隐藏
     */
    protected void hideLoadingDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        mDialog = null;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public ViewGroup getContainer() {
        return container;
    }
}
