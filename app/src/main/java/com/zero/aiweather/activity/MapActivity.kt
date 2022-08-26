package com.zero.aiweather.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.baidu.location.BDLocation
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.geocode.GeoCodeOption
import com.baidu.mapapi.search.geocode.GeoCodeResult
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.scwang.smartrefresh.layout.util.DensityUtil
import com.zero.aiweather.R
import com.zero.aiweather.adapter.DailyAdapter
import com.zero.aiweather.adapter.MapBaseNowAdapter
import com.zero.aiweather.contract.MapWeatherContract
import com.zero.aiweather.databinding.ActivityMapBinding
import com.zero.aiweather.location.LocateHelper
import com.zero.aiweather.location.LocationListener
import com.zero.aiweather.location.MapListener
import com.zero.aiweather.model.*
import com.zero.aiweather.presenter.MapWeatherPresenter
import com.zero.aiweather.utils.GPSUtil
import com.zero.aiweather.utils.StatusBarUtil
import com.zero.aiweather.utils.ToastUtil
import com.zero.aiweather.utils.WeatherUtil
import com.zero.aiweather.widget.NormalItemDecoration
import com.zero.base.baseMvp.MvpActivity

class MapActivity:
        MvpActivity<MapWeatherPresenter>(),
        LocationListener.LocateCallback,
        MapListener.MapCallback,
        MapWeatherContract.IMapWeatherView
{
    private lateinit var binding: ActivityMapBinding
    private lateinit var baseAdapter: MapBaseNowAdapter
    private lateinit var baseModelList: MutableList<MapBaseModel>
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var dailyList: MutableList<Day7Response.Daily>
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var locateHelper: LocateHelper
    private var autoTransition = AutoTransition()
    private var maxHeight = 0
    private var maxWidth = 0

    override fun getLayoutView(): View {
        binding = ActivityMapBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun createPresenter(): MapWeatherPresenter {
        return MapWeatherPresenter()
    }

    override fun initDate(saveInstanceState: Bundle?) {
        initStatusBar()
        initBottomSheet()
        initMap()
        initList()
        initListener()
    }

    private fun initStatusBar() {
        StatusBarUtil.transparencyBar(this)
        StatusBarUtil.StatusBarLightMode(this)
    }

    private fun initBottomSheet() {
        maxHeight = resources.displayMetrics.heightPixels - DensityUtil.dp2px(80f)
        maxWidth = resources.displayMetrics.widthPixels - DensityUtil.dp2px(30f)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.llWeather)
    }

    private fun initMap() {
        locateHelper = LocateHelper(this)
        locateHelper.initLocate(this)
        locateHelper.initMapHelper()
        locateHelper.mapHelper?.let {
            it.bdMap = binding.bdMapView.map
            //开启定位图层
            it.bdMap?.isMyLocationEnabled = true
            it.initMapCallback(this)
            it.initGeoCode()
        }
    }

    private fun initList() {
        baseModelList = mutableListOf()
        val itemDecoration = NormalItemDecoration.Builder()
                .colorResId(ContextCompat.getColor(this, R.color.white_100))
                .height(10)
                .create()
        binding.rlvBaseNow.addItemDecoration(itemDecoration)
        val manager = GridLayoutManager(this, 3)
        binding.rlvBaseNow.layoutManager = manager
        baseAdapter = MapBaseNowAdapter(R.layout.item_recyclerview_map_base, baseModelList)
        binding.rlvBaseNow.adapter = baseAdapter

        dailyList = mutableListOf()
        val dailyManager = LinearLayoutManager(this)
        binding.rlvDaily.layoutManager = dailyManager
        dailyAdapter = DailyAdapter(R.layout.item_recyclerview_day_dark, dailyList, this)
        binding.rlvDaily.adapter = dailyAdapter
    }

    private fun initListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.fabRelocate.setOnClickListener {
            locateHelper.mapHelper?.mapMarker?.remove()
            locateHelper.startLocate()
        }
        binding.ivSearch.setOnClickListener {
            binding.ivBack.visibility = View.GONE
            expandSearchView()
        }
        binding.ivClose.setOnClickListener {
            binding.ivBack.visibility = View.VISIBLE
            binding.etSearch.text = null
            closeSearchView()
        }
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val city = binding.etSearch.text.toString()
                if (city.isNotBlank()) {
                    val geoCodeOption = GeoCodeOption()
                            .city(city)
                            .address(city)
                    locateHelper.mapHelper?.geoCoder?.geocode(geoCodeOption)
                    binding.includeBaseCard.tvLocation.text = city
                } else ToastUtil.showToast("请输入市/县名称")
            }
            return@setOnEditorActionListener false
        }
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_DRAGGING,
                    BottomSheetBehavior.STATE_EXPANDED -> binding.fabRelocate.hide()
                    else -> {
                        binding.fabRelocate.show()
                        val params = bottomSheet.layoutParams
                        if (bottomSheet.height > maxHeight) {
                            params.height = maxHeight
                            bottomSheet.layoutParams = params
                        }
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
    }

    override fun onResume() {
        super.onResume()
        locateHelper.startLocate()
        binding.bdMapView.onResume()
    }

    private fun expandSearchView() {
        binding.etSearch.visibility = View.VISIBLE
        binding.ivClose.visibility = View.VISIBLE
        val params = binding.llSearch.layoutParams
        params.width = maxWidth
        binding.llSearch.layoutParams = params

        beginTransition(binding.llSearch)

        binding.fabRelocate.hide()
    }

    private fun closeSearchView() {
        binding.etSearch.visibility = View.GONE
        binding.ivClose.visibility = View.GONE
        val params = binding.llSearch.layoutParams.also {
            it.width = com.zero.aiweather.utils.DensityUtil.dp2px(50f)
        }
        binding.llSearch.layoutParams = params

        //隐藏键盘
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(window.decorView.windowToken, 0)

        beginTransition(binding.llSearch)

        binding.fabRelocate.show()
    }

    private fun beginTransition(viewGroup: ViewGroup) {
        autoTransition.duration = 500
        TransitionManager.beginDelayedTransition(viewGroup, autoTransition)
    }

    override fun showNowWeather(nowResponse: NowResponse) {
        hideLoadingDialog()
        with(binding.includeBaseCard) {
            tvTemperature.text = String.format(resources.getString(R.string.temperature), nowResponse.now.temp)
            tvDescribe.text = nowResponse.now.text
            tvWind.text = nowResponse.now.windDir
            tvPressure.text = String.format(resources.getString(R.string.pressure), nowResponse.now.pressure)
            ivIcon.setImageResource(WeatherUtil.getWeatherIcon(nowResponse.now.text))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun showBaseWeather(mapBaseModelList: ArrayList<MapBaseModel>) {
        hideLoadingDialog()
        baseModelList.clear()
        baseModelList.addAll(mapBaseModelList)
        baseAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun showDailyWeather(day7Response: Day7Response) {
        hideLoadingDialog()
        dailyList.clear()
        dailyList.addAll(day7Response.daily)
        dailyAdapter.notifyDataSetChanged()
    }

    override fun showAirQuality(airQualityResponse: AirQualityResponse) {
        hideLoadingDialog()
        binding.includeBaseCard.tvAqi.text = String.format(resources.getString(R.string.aqi), airQualityResponse.now.aqi)
    }

    override fun showLifeAdvice(adviceResponse: AdviceResponse) {
        hideLoadingDialog()
        binding.includeBaseCard.tvUltravioletRay.text = adviceResponse.daily[0].category
    }

    override fun onFailure(message: String) {
        ToastUtil.showToastCenter(message)
    }

    override fun locateResult(bdLocation: BDLocation) {
        binding.includeBaseCard.tvLocation.text = bdLocation.district

        locateHelper.stopLocate()
        locateHelper.mapHelper?.let {
            val latLng = it.getLatLng(bdLocation)
            it.bondLocationWithMap(bdLocation)
            it.showMapMarker(latLng)
            it.updateMapStatus(latLng)
            getWeather(latLng)
        }
    }

    override fun mapClick(latLng: LatLng) {
        updateMap(latLng)
        val reverseOption = ReverseGeoCodeOption()
                .location(latLng)
                .pageNum(0)
                .pageSize(100)
        locateHelper.mapHelper?.geoCoder?.reverseGeoCode(reverseOption)
    }

    override fun geoCodeResult(geoCode: GeoCodeResult) {
        val latLng = geoCode.location
        updateMap(latLng)
        getWeather(latLng)
    }

    override fun geoReverseResult(reverseResult: ReverseGeoCodeResult) {
        binding.includeBaseCard.tvLocation.text = reverseResult.addressDetail.district
        getWeather(reverseResult.location)
    }

    private fun updateMap(latLng: LatLng) {
        locateHelper.mapHelper?.bdMap?.clear()
        locateHelper.mapHelper?.showMapMarker(latLng)
        locateHelper.mapHelper?.updateMapStatus(latLng)
    }

    private fun getWeather(latLng: LatLng) {
        showLoadingDialog()
        val location = "${GPSUtil.doubleToString(latLng.longitude)},${GPSUtil.doubleToString(latLng.latitude)}"
        with(mPresenter) {
            getNowWeather(location)
            getDailyWeather(location)
            getAirQuality(location)
            getAdviceWeather(location)
        }
    }

    override fun onPause() {
        super.onPause()
        binding.bdMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        locateHelper.release()
        binding.bdMapView.onDestroy()
    }

}