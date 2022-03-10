package com.zero.aiweather.contract;

import com.zero.aiweather.model.response.AirNowResponse;
import com.zero.aiweather.model.response.Day7Response;
import com.zero.aiweather.model.response.Hour24Response;
import com.zero.aiweather.model.response.NowResponse;

public class WeatherContract {
    public interface IPresenter {
        void getWeatherNow(String location);
        void getWeather24Hour(String location);
        void getWeather7Day(String location);
        void getAirNow(String location);
    }

    public interface IView {
        void showWeatherNow(NowResponse nowResponse);
        void showWeather24Hour(Hour24Response hourResponse);
        void showWeather7Day(Day7Response dayResponse);
        void showAirNow(AirNowResponse airNowResponse);
    }
}
