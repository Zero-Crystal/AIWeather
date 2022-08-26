package com.zero.aiweather.contract;

import com.zero.aiweather.model.CitySearchResponse;
import com.zero.base.base.IBaseView;

public class SearchContract {

    public interface IPresenter {
        /**
         * 搜索城市LocationId
         * */
        void searchCity(String district);
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
