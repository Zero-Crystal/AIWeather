package com.zero.aiweather.location

import android.content.Context
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.zero.aiweather.utils.GPSUtil

class LocateHelper(
        var context: Context
) {
    private lateinit var locationListener: LocationListener
    private lateinit var locationClient: LocationClient

    var mapHelper: MapHelper? = null

    fun initMapHelper() {
        if (mapHelper == null) {
            mapHelper = MapHelper(context)
        }
    }

    fun initLocate(locateCallback: LocationListener.LocateCallback) {
        locationListener = LocationListener()
        locationListener.locationCallback = locateCallback
        val options = LocationClientOption().apply {
            //可选，是否需要地址信息
            setIsNeedAddress(true)
            //可选，设置是否需要最新版本的地址信息
            setNeedNewVersionRgc(true)
            //设置坐标类型，可以设置BD09LL和GCJ02两种坐标
            setCoorType("bd09ll")
        }
        locationClient = LocationClient(context)
        locationClient.locOption = options
        locationClient.registerLocationListener(locationListener)
    }

    fun isGpsOpen(): Boolean {
        return GPSUtil.isOPen(context)
    }

    fun startLocate() {
        locationClient.start()
    }

    fun stopLocate() {
        locationClient.stop()
    }

    fun release() {
        stopLocate()
        mapHelper?.release()
    }
}