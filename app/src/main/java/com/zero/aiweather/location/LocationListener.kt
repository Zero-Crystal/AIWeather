package com.zero.aiweather.location

import com.baidu.location.BDLocation
import com.baidu.location.BDLocationListener

class LocationListener : BDLocationListener {
    var locationResult: LocateResult? = null

    override fun onReceiveLocation(p0: BDLocation?) {
        if (p0 != null) {
            locationResult?.result(p0)
        }
    }

    interface LocateResult {
        fun result(bdLocation: BDLocation)
    }
}