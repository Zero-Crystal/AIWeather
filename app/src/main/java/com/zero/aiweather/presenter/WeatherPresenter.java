package com.zero.aiweather.presenter;

import android.annotation.SuppressLint;

import com.zero.aiweather.contract.WeatherContract;
import com.zero.aiweather.model.AdviceModel;
import com.zero.aiweather.model.AdviceResponse;
import com.zero.aiweather.model.AirQualityResponse;
import com.zero.aiweather.model.CitySearchResponse;
import com.zero.aiweather.model.HourlyResponse;
import com.zero.aiweather.model.MoonResponse;
import com.zero.aiweather.model.SunResponse;
import com.zero.aiweather.netApi.ApiService;
import com.zero.aiweather.model.Day7Response;
import com.zero.aiweather.model.NowResponse;
import com.zero.aiweather.utils.WeatherUtil;
import com.zero.base.base.BasePresenter;
import com.zero.base.baseNewNet.NetWorkApi;
import com.zero.base.baseNewNet.observer.BaseObserver;
import com.zero.base.util.KLog;

import java.util.ArrayList;
import java.util.List;

public class WeatherPresenter extends BasePresenter<WeatherContract.IWeatherView> implements WeatherContract.IPresenter {

    @Override
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

    @Override
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

    @Override
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

    public void getTemperatureList(Day7Response day7Response) {
        List<Integer> maxTempList = new ArrayList<>();
        List<Integer> minTempList = new ArrayList<>();
        for (Day7Response.Daily daily : day7Response.getDaily()) {
            maxTempList.add(Integer.valueOf(daily.getTempMax()));
            minTempList.add(Integer.valueOf(daily.getTempMin()));
        }
        if (getView() != null) {
            getView().showWeatherMaxMinTemp(maxTempList, minTempList);
        }
    }

    @Override
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

    @Override
    @SuppressLint("CheckResult")
    public void getLifeAdvice(String location) {
        ApiService apiService = NetWorkApi.createService(ApiService.class);
        apiService.getLifeAdvice(location, "1,3,6,9,13,16").compose(NetWorkApi.applySchedulers(new BaseObserver<AdviceResponse>() {
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

    @Override
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

    @Override
    @SuppressLint("CheckResult")
    public void getSunTime(String location, String date) {
        ApiService apiService = NetWorkApi.createService(ApiService.class);
        apiService.getSunTime(location, date).compose(NetWorkApi.applySchedulers(new BaseObserver<SunResponse>() {
            @Override
            public void onSuccess(SunResponse sunResponse) {
                if (sunResponse != null) {
                    String sunRiseTime = sunResponse.getSunrise().substring(0, 10)
                            + " " + sunResponse.getSunrise().substring(11, 16);
                    String sunSetTime = sunResponse.getSunset().substring(0, 10)
                            + " " + sunResponse.getSunset().substring(11, 16);
                    sunResponse.setSunrise(sunRiseTime);
                    sunResponse.setSunset(sunSetTime);
                    if (getView() != null) {
                        getView().showSunRiseAndSet(sunResponse);
                    }
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

    @Override
    @SuppressLint("CheckResult")
    public void getMoonTime(String location, String date) {
        ApiService apiService = NetWorkApi.createService(ApiService.class);
        apiService.getMoonTime(location, date).compose(NetWorkApi.applySchedulers(new BaseObserver<MoonResponse>() {
            @Override
            public void onSuccess(MoonResponse moonResponse) {
                if (moonResponse != null) {
                    String moonRiseTime = moonResponse.getMoonrise().substring(0, 10)
                            + " " + moonResponse.getMoonrise().substring(11, 16);
                    String moonSetTime = moonResponse.getMoonset().substring(0, 10)
                            + " " + moonResponse.getMoonset().substring(11, 16);
                    moonResponse.setMoonrise(moonRiseTime);
                    moonResponse.setMoonset(moonSetTime);
                    if (getView() != null) {
                        getView().showMoonRiseAndSet(moonResponse);
                    }
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
