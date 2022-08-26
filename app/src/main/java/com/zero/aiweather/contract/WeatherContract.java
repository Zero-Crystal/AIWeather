package com.zero.aiweather.contract;

import com.zero.aiweather.model.AdviceModel;
import com.zero.aiweather.model.AirQualityResponse;
import com.zero.aiweather.model.CitySearchResponse;
import com.zero.aiweather.model.Day7Response;
import com.zero.aiweather.model.HourlyResponse;
import com.zero.aiweather.model.MoonResponse;
import com.zero.aiweather.model.NowResponse;
import com.zero.aiweather.model.SunResponse;
import com.zero.base.base.IBaseView;

import java.util.List;

/**
 * 天气订阅类
 * */
public class WeatherContract {

    public interface IPresenter {
        /**
         * 获取实时天气
         * */
        void getWeatherNow(String location);
        /**
         * 获取逐小时预报
         * */
        void getWeatherHourly(String location);
        /**
         * 获取7天天气预报
         * */
        void getWeatherDay7(String location);
        /**
         * 获取实时空气质量
         * */
        void getAirQuality(String location);
        /**
         * 获取生活建议
         * */
        void getLifeAdvice(String location);
        /**
         * 获取城市locationId
         * */
        void getCityId(String district);
        /**
         * 获取日出日落
         * */
        void getSunTime(String location, String date);
        /**
         * 获取月升月落
         * */
        void getMoonTime(String location, String date);
    }

    public interface IWeatherView extends IBaseView {
        /**
         * 展示实时天气
         * */
        void showWeatherNow(NowResponse nowResponse);
        /**
         * 展示逐小时预报
         * */
        void showWeatherHourly(HourlyResponse hourlyResponse);
        /**
         * 展示7天预报
         * */
        void showWeather7Day(Day7Response day7Response);
        /**
         * 展示7天高/低温预测
         * */
        void showWeatherMaxMinTemp(List<Integer> maxTempList, List<Integer> minTempList);
        /**
         * 展示空气质量
         * */
        void showAirQuality(AirQualityResponse airQualityResponse);
        /**
         * 展示生活指数
         * */
        void showLiftAdvice(List<AdviceModel> adviceModelList);
        /**
         * 展示城市id搜索
         * */
        void showCityId(CitySearchResponse citySearchResponse);
        /**
         * 展示日升日落
         * */
        void showSunRiseAndSet(SunResponse sunResponse);
        /**
         * 展示月升月落
         * */
        void showMoonRiseAndSet(MoonResponse moonResponse);
        /**
         * 请求失败
         * */
        void onFailure(String message);
    }
}
