package com.zero.base.base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.fragment.app.Fragment;

import com.zero.base.AIWeatherApplication;

public class BaseFragment extends Fragment {
    private ProgressDialog dialog;

    public void startProgress(Context context) {
        if (dialog == null) {
            dialog = new ProgressDialog(context);
            dialog.setTitle("正在加载中···");
            dialog.setCancelable(true);
        }
        dialog.show();
    }

    public void dismissProgress() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
