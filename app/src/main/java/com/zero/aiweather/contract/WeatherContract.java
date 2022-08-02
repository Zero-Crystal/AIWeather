package com.zero.aiweather.contract;

import android.annotation.SuppressLint;

import com.zero.aiweather.model.AdviceModel;
import com.zero.aiweather.model.AdviceResponse;
import com.zero.aiweather.model.AirQualityResponse;
import com.zero.aiweather.model.CitySearchResponse;
import com.zero.aiweather.model.HourlyResponse;
import com.zero.aiweather.netApi.ApiService;
import com.zero.aiweather.model.Day7Response;
import com.zero.aiweather.model.NowResponse;
import com.zero.aiweather.utils.WeatherUtil;
import com.zero.base.base.BasePresenter;
import com.zero.base.base.IBaseView;
import com.zero.base.baseNewNet.NetWorkApi;
import com.zero.base.baseNewNet.observer.BaseObserver;
import com.zero.base.util.KLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 天气订阅类
 * */
public class WeatherContract {
    public static class WeatherPresenter extends BasePresenter<IWeatherView> {
        /**
         * 获取实时天气
         * */
        @SuppressLint("CheckResult")
        public void getWeatherNow(String location) {
            ApiService apiService = NetWorkApi.createService(ApiService.class);
            apiService.getWeatherNow(location).compose(NetWorkApi.applySchedulers(new BaseObserver<NowResponse>() {
                @Override
                public void onSuccess(NowResponse nowResponse) {
                    if (getView() != null) {
                        getView().showWeatherNow(nowResponse);
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

        /**
         * 获取逐小时预报
         * */
        @SuppressLint("CheckResult")
        public void getWeatherHourly(String location) {
            ApiService apiService = NetWorkApi.createService(ApiService.class);
            apiService.getWeatherHourly(location).compose(NetWorkApi.applySchedulers(new BaseObserver<HourlyResponse>() {
                @Override
                public void onSuccess(HourlyResponse hourlyResponse) {
                    if (getView() != null) {
                        getView().showWeatherHourly(hourlyResponse);
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

        /**
         * 获取7天天气预报
         * */
        @SuppressLint("CheckResult")
        public void getWeatherDay7(String location) {
            ApiService apiService = NetWorkApi.createService(ApiService.class);
            apiService.getWeather7Day(location).compose(NetWorkApi.applySchedulers(new BaseObserver<Day7Response>() {
                @Override
                public void onSuccess(Day7Response day7Response) {
                    if (getView() != null) {
                        getView().showWeather7Day(day7Response);
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

        /**
         * 获取实时空气质量
         * */
        @SuppressLint("CheckResult")
        public void getAirQuality(String location) {
            ApiService apiService = NetWorkApi.createService(ApiService.class);
            apiService.getAirQuality(location).compose(NetWorkApi.applySchedulers(new BaseObserver<AirQualityResponse>() {
                @Override
                public void onSuccess(AirQualityResponse airQualityResponse) {
                    if (getView() != null) {
                        getView().showAirQuality(airQualityResponse);
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

        /**
         * 获取生活建议
         * */
        @SuppressLint("CheckResult")
        public void getLifeAdvice(String location) {
            ApiService apiService = NetWorkApi.createService(ApiService.class);
            apiService.getLifeAdvice(location).compose(NetWorkApi.applySchedulers(new BaseObserver<AdviceResponse>() {
                @Override
                public void onSuccess(AdviceResponse adviceResponse) {
                    if (getView() != null) {
                        getView().showLiftAdvice(getAdviceModelList(adviceResponse));
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

        @SuppressLint("CheckResult")
        public void getCityId(String district) {
            ApiService apiService = NetWorkApi.createService(ApiService.class, 2);
            apiService.getCityId(district).compose(NetWorkApi.applySchedulers(new BaseObserver<CitySearchResponse>() {
                @Override
                public void onSuccess(CitySearchResponse citySearchResponse) {
                    if (getView() != null) {
                        getView().showCityId(citySearchResponse);
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

        private List<AdviceModel> getAdviceModelList(AdviceResponse adviceResponse) {
            List<AdviceModel> adviceModelList = new ArrayList<>();
            for (AdviceResponse.Daily advice : adviceResponse.getDaily()) {
                AdviceModel adviceModel = new AdviceModel();
                adviceModel.setAdviceDaily(advice);
                adviceModel.setTitle(advice.getName().substring(0, 2));
                adviceModel.setIconId(WeatherUtil.getAdviceIcon(advice.getType()));
                adviceModelList.add(adviceModel);
            }
            return adviceModelList;
        }
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
         * 展示空气质量
         * */
        void showAirQuality(AirQualityResponse airQualityResponse);
        /**
         * 展示生活指数
         * */
        void showLiftAdvice(List<AdviceModel> adviceModelList);
//        void showLiftAdvice(AdviceResponse adviceResponse);
        /**
         * 展示城市id搜索
         * */
        void showCityId(CitySearchResponse citySearchResponse);
        /**
         * 请求失败
         * */
        void onFailure(String message);
    }
}
