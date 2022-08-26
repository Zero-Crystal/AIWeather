package com.zero.aiweather.contract

import com.zero.aiweather.model.*
import com.zero.base.base.BasePresenter
import com.zero.base.base.IBaseView

class MapWeatherContract {
    interface IMapPresenter {
        fun getNowWeather(location: String)

        fun getDailyWeather(location: String)

        fun getAirQuality(location: String)

        fun getAdviceWeather(location: String)
    }

    interface IMapWeatherView: IBaseView {
        fun showNowWeather(nowResponse: NowResponse)

        fun showBaseWeather(mapBaseModelList: ArrayList<MapBaseModel>)

        fun showDailyWeather(day7Response: Day7Response)

        fun showAirQuality(airQualityResponse: AirQualityResponse)

        fun showLifeAdvice(adviceResponse: AdviceResponse)

        fun onFailure(message: String)
    }
}