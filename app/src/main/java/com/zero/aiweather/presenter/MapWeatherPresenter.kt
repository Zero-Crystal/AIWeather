package com.zero.aiweather.presenter

import android.annotation.SuppressLint
import com.zero.aiweather.R
import com.zero.aiweather.contract.MapWeatherContract
import com.zero.aiweather.model.*
import com.zero.aiweather.netApi.ApiService
import com.zero.base.base.BasePresenter
import com.zero.base.baseNewNet.NetWorkApi
import com.zero.base.baseNewNet.observer.BaseObserver
import com.zero.base.util.KLog

class MapWeatherPresenter :
    BasePresenter<MapWeatherContract.IMapWeatherView>(),
    MapWeatherContract.IMapPresenter {
    @SuppressLint("CheckResult")
    override fun getNowWeather(location: String) {
        NetWorkApi.createService(ApiService::class.java).let {
            it.getWeatherNow(location).compose(NetWorkApi.applySchedulers(
                object : BaseObserver<NowResponse>() {
                    override fun onSuccess(t: NowResponse?) {
                        t?.let {
                            view?.showNowWeather(it)
                            getMapBaseModelList(it)
                        }
                    }

                    override fun onFailure(e: Throwable?) {
                        e?.run {
                            printStackTrace()
                            message?.let {
                                view?.onFailure(it)
                                KLog.e("数据请求失败！$it")
                            }
                        }
                    }
                }
            ))
        }
    }

    @SuppressLint("CheckResult")
    override fun getDailyWeather(location: String) {
        NetWorkApi.createService(ApiService::class.java).let {
            it.getWeather7Day(location).compose(NetWorkApi.applySchedulers(
                object : BaseObserver<Day7Response>() {
                    override fun onSuccess(t: Day7Response?) {
                        t?.let { view?.showDailyWeather(it) }
                    }

                    override fun onFailure(e: Throwable?) {
                        e?.run {
                            printStackTrace()
                            message?.let {
                                view?.onFailure(it)
                                KLog.e("数据请求失败！$it")
                            }
                        }
                    }
                }
            ))
        }
    }

    @SuppressLint("CheckResult")
    override fun getAirQuality(location: String) {
        NetWorkApi.createService(ApiService::class.java).let { it ->
            it.getAirQuality(location).compose(NetWorkApi.applySchedulers(
                object : BaseObserver<AirQualityResponse>() {
                    override fun onSuccess(t: AirQualityResponse?) {
                        t?.let { view?.showAirQuality(t) }
                    }

                    override fun onFailure(e: Throwable?) {
                        e?.run {
                            printStackTrace()
                            message?.let {
                                view?.onFailure(it)
                                KLog.e("数据请求失败！$it")
                            }
                        }
                    }
                }
            ))
        }
    }

    @SuppressLint("CheckResult")
    override fun getAdviceWeather(location: String) {
        NetWorkApi.createService(ApiService::class.java).let {
            it.getLifeAdvice(location, "5").compose(NetWorkApi.applySchedulers(
                object : BaseObserver<AdviceResponse>() {
                    override fun onSuccess(t: AdviceResponse?) {
                        t?.let { view?.showLifeAdvice(it) }
                    }

                    override fun onFailure(e: Throwable?) {
                        e?.run {
                            printStackTrace()
                            message?.let {
                                view?.onFailure(it)
                                KLog.e("数据请求失败！$it")
                            }
                        }
                    }
                }
            ))
        }
    }

    fun getMapBaseModelList(nowResponse: NowResponse) {
        var baseModelList = arrayListOf<MapBaseModel>().apply {
            add(MapBaseModel("体感温度", "${nowResponse.now.feelsLike}°", R.drawable.ic_temperature))
            add(MapBaseModel("降水量", "${nowResponse.now.precip}mm", R.drawable.ic_rain))
            add(MapBaseModel("湿度", "${nowResponse.now.humidity}%", R.drawable.ic_humidity))
            add(MapBaseModel("大气压强", "${nowResponse.now.pressure}hpa", R.drawable.ic_pressure))
            add(MapBaseModel("能见度", "${nowResponse.now.vis}KM", R.drawable.ic_invisible))
            add(MapBaseModel("云量", "${nowResponse.now.cloud}%", R.drawable.ic_cloud))
        }
        view?.showBaseWeather(baseModelList)
    }
}