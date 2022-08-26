package com.zero.aiweather.contract;

import com.zero.aiweather.model.BiYingImgResponse;
import com.zero.aiweather.model.ProvinceResponse;
import com.zero.base.base.IBaseView;

import java.util.List;

public class MainContract {
    public interface IPresenter{
        /**
         * 必应每日一图
         * */
        void getBiYinImage();
    }

    public interface IMainView extends IBaseView {
        /**
         * 展示必应每日一图
         * */
        void showBiYinImage(BiYingImgResponse biYingImgResponse);
        /**
         * 展示切换城市弹窗
         * */
        void showCityPopWindow(List<ProvinceResponse> provinceList);
        /**
         * 请求失败
         * */
        void onFailure(String message);
    }
}
