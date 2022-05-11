package com.zero.aiweather.contract;

import android.annotation.SuppressLint;

import com.zero.aiweather.model.CitySearchResponse;
import com.zero.aiweather.netApi.ApiService;
import com.zero.base.base.BasePresenter;
import com.zero.base.base.IBaseView;
import com.zero.base.baseNewNet.NetWorkApi;
import com.zero.base.baseNewNet.observer.BaseObserver;
import com.zero.base.util.KLog;

public class SearchContract {
    public static class SearchPresenter extends BasePresenter<ISearchView>{
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

    public interface ISearchView extends IBaseView {
        /**
         * 展示搜索结果
         * */
        void showSearchResult(CitySearchResponse citySearchResponse);
        /**
         * 请求失败
         * */
        void onFailure(String message);
    }
}
