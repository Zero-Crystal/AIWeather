package com.zero.base.baseNet;

import com.google.gson.Gson;
import com.zero.base.baseNewNet.BaseResponse;
import com.zero.base.util.KLog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 网络请求回调
 * */
public abstract class NetCallBack<T> implements Callback<T> {

    /**
     * 访问成功
     * */
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response != null && response.body() != null && response.isSuccessful()) {
            BaseResponse baseResponse = new Gson().fromJson(new Gson().toJson(response.body()), BaseResponse.class);
            if (baseResponse.responseCode == 404) {//404
                KLog.e("Warn",baseResponse.responseError);
            } else if(baseResponse.responseCode == 500) {//500
                KLog.e("Warn",baseResponse.responseError);
            } else {//无异常则返回数据
                onSuccess(call, response);
                KLog.e("Warn","其他情况");
            }
        } else {
            onFailure();
        }
    }

    /**
     * 访问失败
     * */
    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onFailure();
    }

    /**
     * 请求成功
     * */
    public abstract void onSuccess(Call<T> call, Response<T> response);
    /**
     * 请求失败
     * */
    public abstract void onFailure();
}
