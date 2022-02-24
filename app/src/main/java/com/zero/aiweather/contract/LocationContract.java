package com.zero.aiweather.contract;

import com.zero.aiweather.model.response.CityResponse;

public class LocationContract {
    public interface IPresenter {
        void getLocationId(String cityName);
    }

    public interface IView {
        void getCityNameList(CityResponse response);
    }
}
