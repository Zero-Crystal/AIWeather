package com.zero.aiweather.location

import android.content.Context
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.zero.aiweather.utils.GPSUtil
import com.zero.aiweather.utils.ToastUtil
import com.zero.base.util.KLog

class LocateHelper(var context: Context) {
    var locationClient: LocationClient? = null
    var locationListener: LocationListener? = null

    fun initLocate(locateResult: LocationListener.LocateResult) {
        locationListener = LocationListener()
        locationListener!!.locationResult = locateResult

        locationClient = LocationClient(context)
        val options = LocationClientOption()
        //可选，是否需要地址信息
        options.setIsNeedAddress(true)
        //可选，设置是否需要最新版本的地址信息
        options.setNeedNewVersionRgc(true)
        locationClient!!.locOption = options
        locationClient!!.registerLocationListener(locationListener)
    }

    fun startLocate(): Boolean {
        return if (GPSUtil.isOPen(context)) {
            KLog.i("---------------------> 定位中...")
            locationClient!!.start()
            true
        } else {
            KLog.i("---------------------> 定位未打开")
            ToastUtil.showToast("定位未打开")
            false
        }
    }

    fun stopLocate() {
        locationClient!!.stop()
    }
}