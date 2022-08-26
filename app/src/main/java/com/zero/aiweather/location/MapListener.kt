package com.zero.aiweather.location

import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapPoi
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.core.SearchResult
import com.baidu.mapapi.search.geocode.GeoCodeResult
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult
import com.zero.aiweather.utils.ToastUtil

class MapListener: BaiduMap.OnMapClickListener, OnGetGeoCoderResultListener {
    var mapCallback: MapCallback? = null

    override fun onMapPoiClick(p0: MapPoi?) {
    }

    override fun onMapClick(p0: LatLng?) {
        p0?.let {
            mapCallback?.mapClick(p0)
        }
    }

    override fun onGetGeoCodeResult(geoCodeResult: GeoCodeResult?) {
        if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            ToastUtil.showToast("请输入市/县名称")
            return
        }
        mapCallback?.geoCodeResult(geoCodeResult)
    }

    override fun onGetReverseGeoCodeResult(reverseGeoCodeResult: ReverseGeoCodeResult?) {
        if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            return
        }
        mapCallback?.geoReverseResult(reverseGeoCodeResult)
    }

    interface MapCallback {
        /**
         * 地理编码结果回调
         * */
        fun geoCodeResult(geoCode: GeoCodeResult)
        /**
         * 地理编码反编译结果回调
         * */
        fun geoReverseResult(reverseResult: ReverseGeoCodeResult)
        /**
         * 地图点击回调
         * */
        fun mapClick(latLng: LatLng)
    }
}