package com.zero.aiweather.location

import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.BDLocationListener

class LocationListener : BDAbstractLocationListener() {
    var locationCallback: LocateCallback? = null

    override fun onReceiveLocation(p0: BDLocation?) {
        if (p0 != null) {
            locationCallback?.locateResult(p0)
        }
    }

    interface LocateCallback {
        /**
         * 百度地图定位结果回调
         * */
        fun locateResult(bdLocation: BDLocation)
    }
}