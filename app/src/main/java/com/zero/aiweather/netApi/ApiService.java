package com.zero.aiweather.netApi;

import com.zero.aiweather.model.AdviceResponse;
import com.zero.aiweather.model.AirQualityResponse;
import com.zero.aiweather.model.BiYingImgResponse;
import com.zero.aiweather.model.CitySearchResponse;
import com.zero.aiweather.model.Day7Response;
import com.zero.aiweather.model.HourlyResponse;
import com.zero.aiweather.model.MoonResponse;
import com.zero.aiweather.model.NowResponse;
import com.zero.aiweather.model.SunResponse;
import com.zero.aiweather.utils.Constant;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * api接口集合
 * */
public interface ApiService {
    /**
     * 实时天气
     * */
    @GET("weather/now?key=" + Constant.ApiKey)
    Observable<NowResponse> getWeatherNow(@Query("location") String location);

    /**
     * 逐小时预报
     * */
    @GET("weather/24h?key=" + Constant.ApiKey)
    Observable<HourlyResponse> getWeatherHourly(@Query("location") String location);

    /**
     * 7天天气预报
     * */
    @GET("weather/7d?key=" + Constant.ApiKey)
    Observable<Day7Response> getWeather7Day(@Query("location") String location);

    /**
     * 生活指数
     * */
    @GET("indices/1d?key=" + Constant.ApiKey + "&type=1,3,6,9,13,16")
    Observable<AdviceResponse> getLifeAdvice(@Query("location") String location);

    /**
     * 实时空气质量
     * */
    @GET("air/now?key=" + Constant.ApiKey)
    Observable<AirQualityResponse> getAirQuality(@Query("location") String location);

    /**
     * 查询城市
     * */
    @GET("city/lookup?key=" + Constant.ApiKey)
    Observable<CitySearchResponse> getCityId(@Query("location") String district);

    /**
     * 日升日落
     * */
    @GET("astronomy/sun?key=" + Constant.ApiKey)
    Observable<SunResponse> getSunTime(@Query("location") String location, @Query("date") String date);

    /**
     * 月升月落
     * */
    @GET("astronomy/moon?key=" + Constant.ApiKey)
    Observable<MoonResponse> getMoonTime(@Query("location") String location, @Query("date") String date);

    /**
     * 必应每日一图
     * */
    @GET("HPImageArchive.aspx?format=js&idx=0&n=1")
    Observable<BiYingImgResponse> getBiYingImage();
}
