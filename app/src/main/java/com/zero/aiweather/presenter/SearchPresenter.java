package com.zero.aiweather.presenter;

import android.annotation.SuppressLint;

import com.zero.aiweather.contract.SearchContract;
import com.zero.aiweather.model.CitySearchResponse;
import com.zero.aiweather.netApi.ApiService;
import com.zero.base.base.BasePresenter;
import com.zero.base.baseNewNet.NetWorkApi;
import com.zero.base.baseNewNet.observer.BaseObserver;
import com.zero.base.util.KLog;

public class SearchPresenter extends BasePresenter<SearchContract.ISearchView> implements SearchContract.IPresenter {

    @Override
    @SuppressLint("CheckResult")
    public void searchCity(String district) {
        ApiService apiService = NetWorkApi.createService(ApiService.class, 2);
        apiService.getCityId(district).compose(NetWorkApi.applySchedulers(new BaseObserver<CitySearchResponse>() {
            @Override
            public void onSuccess(CitySearchResponse citySearchResponse) {
                if (getView() != null) {
                    getView().showSearchResult(citySearchResponse);
                }
            }

            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();
                if (getView() != null) {
                    getView().onFailure("数据请求失败！");
                }
                KLog.e("数据请求失败！" + e.getMessage());
            }
        }));
    }
}
