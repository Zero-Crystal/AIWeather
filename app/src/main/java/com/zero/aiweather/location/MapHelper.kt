package com.zero.aiweather.location

import android.content.Context
import android.graphics.BitmapFactory
import com.baidu.location.BDLocation
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.geocode.GeoCoder
import com.zero.aiweather.R

class MapHelper(
        var context: Context
) {
    private var mapListener: MapListener = MapListener()
    var geoCoder: GeoCoder? = null
    var bdMap: BaiduMap? = null
    var mapMarker: Marker? = null

    fun initMapCallback(mapCallback: MapListener.MapCallback) {
        mapListener.mapCallback = mapCallback
        bdMap?.setOnMapClickListener(mapListener)
    }

    fun initGeoCode() {
        if (geoCoder == null) geoCoder = GeoCoder.newInstance()
        geoCoder?.setOnGetGeoCodeResultListener(mapListener)
    }

    fun bondLocationWithMap(bdLocation: BDLocation) {
        val locationDate = MyLocationData.Builder()//定位构造器
                .accuracy(bdLocation.radius)//设置定位数据的精度信息，单位：米
                .direction(bdLocation.direction)//设置定位数据的方向信息
                .latitude(bdLocation.latitude)//设置定位数据的纬度
                .longitude(bdLocation.longitude)//设置定位数据的经度
                .build()
        //设置定位数据，只有开启定位图层之后才会生效
        bdMap?.setMyLocationData(locationDate)
    }

    fun getLatLng(bdLocation: BDLocation): LatLng = LatLng(bdLocation.latitude, bdLocation.longitude)

    fun updateMapStatus(latLng: LatLng) {
        //地图状态构造器
        val statusBuilder = MapStatus.Builder()
                .target(latLng)//传入经纬度坐标
                .zoom(13.0f)//设置缩放比例尺，13 表示：比例尺/2000米 2公里
        //改变地图状态，使用地图状态更新工厂中的新地图状态方法，传入状态构造器
        bdMap?.animateMapStatus(MapStatusUpdateFactory.newMapStatus(statusBuilder.build()))
    }

    fun showMapMarker(latLng: LatLng) {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.icon_locate_mark)
        //创建标点marker设置对象
        val markerOption = MarkerOptions()
                .position(latLng)//设置标点的定位
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))//设置标点图标
        //标点
        mapMarker = bdMap?.addOverlay(markerOption) as Marker
    }

    fun release() {
        // 关闭定位图层
        bdMap?.isMyLocationEnabled = false
        bdMap = null
        mapMarker = null
    }
}