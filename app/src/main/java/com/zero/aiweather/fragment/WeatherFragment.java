package com.zero.aiweather.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.baidu.location.BDLocation;
import com.zero.aiweather.R;
import com.zero.aiweather.adapter.Advice2Adapter;
import com.zero.aiweather.adapter.DailyTopAdapter;
import com.zero.aiweather.adapter.HourlyAdapter;
import com.zero.aiweather.presenter.WeatherPresenter;
import com.zero.aiweather.databinding.FragmentWeatherBinding;
import com.zero.aiweather.databinding.PopupWindowAdviceBinding;
import com.zero.aiweather.location.LocateHelper;
import com.zero.aiweather.location.LocationListener;
import com.zero.aiweather.model.AdviceModel;
import com.zero.aiweather.model.AirQualityResponse;
import com.zero.aiweather.model.CitySearchResponse;
import com.zero.aiweather.model.Day7Response;
import com.zero.aiweather.model.HourlyResponse;
import com.zero.aiweather.model.MoonResponse;
import com.zero.aiweather.model.NowResponse;
import com.zero.aiweather.model.SunResponse;
import com.zero.aiweather.contract.WeatherContract;
import com.zero.aiweather.utils.AnimationUtil;
import com.zero.aiweather.utils.Constant;
import com.zero.aiweather.utils.DensityUtil;
import com.zero.aiweather.utils.GPSUtil;
import com.zero.aiweather.utils.PopupWindowUtil;
import com.zero.aiweather.utils.SPUtil;
import com.zero.aiweather.utils.ToastUtil;
import com.zero.aiweather.utils.WeatherUtil;
import com.zero.aiweather.viewModel.MainViewModel;
import com.zero.aiweather.widget.NormalItemDecoration;
import com.zero.base.baseMvp.MvpFragment;
import com.zero.base.util.DateUtil;
import com.zero.base.util.KLog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WeatherFragment extends MvpFragment<WeatherPresenter> implements WeatherContract.IWeatherView, LocationListener.LocateCallback {
    private FragmentWeatherBinding binding;
    private LocateHelper locateHelper;
    private final List<Day7Response.Daily> forecastList = new ArrayList<>();
    private DailyTopAdapter topAdapter;
    private final List<AdviceModel> adviceList = new ArrayList<>();
    private Advice2Adapter advice2Adapter;
    private final List<HourlyResponse.Hourly> hourlyList = new ArrayList<>();
    private HourlyAdapter hourlyAdapter;
    private String location;
    private boolean lastCpVisibleState = false;
    private boolean lastSvVisibleState = false;
    private boolean lastMvVisibleState = false;

    private AnimationUtil animUtil;
    private MainViewModel mainViewModel;

    @Override
    protected WeatherPresenter createPresenter() {
        return new WeatherPresenter();
    }

    @Override
    public View getLayoutView() {
        binding = FragmentWeatherBinding.inflate(getInflater());
        return binding.getRoot();
    }

    @Override
    public void initDate(Bundle saveInstanceState) {
        animUtil = new AnimationUtil();
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        //初始化定位
        locateHelper = new LocateHelper(context);
        locateHelper.initLocate(this);
        initList();
        initListener();
        startLocate();
    }

    private void initList() {
        NormalItemDecoration dailyDecoration = new NormalItemDecoration.Builder()
                .colorResId(ContextCompat.getColor(context, R.color.white_100)).height(10).create();
        GridLayoutManager dailyManager = new GridLayoutManager(context, 7);
        topAdapter = new DailyTopAdapter(R.layout.item_recyclerview_daily_top, forecastList);
        binding.includeDailyForecast.rvDailyTop.addItemDecoration(dailyDecoration);
        binding.includeDailyForecast.rvDailyTop.setLayoutManager(dailyManager);
        binding.includeDailyForecast.rvDailyTop.setAdapter(topAdapter);

        LinearLayoutManager hourlyManager = new LinearLayoutManager(context);
        hourlyManager.setOrientation(RecyclerView.HORIZONTAL);
        hourlyAdapter = new HourlyAdapter(R.layout.item_recyclerview_hour, hourlyList);
        binding.rvHourly.setLayoutManager(hourlyManager);
        binding.rvHourly.setAdapter(hourlyAdapter);

        NormalItemDecoration adviceDecoration = new NormalItemDecoration.Builder()
                .colorResId(ContextCompat.getColor(context, R.color.black_90)).height(5).create();
        GridLayoutManager adviceManager = new GridLayoutManager(context, 3);
        advice2Adapter = new Advice2Adapter(R.layout.item_recyclerview_advice, adviceList, context);
        binding.rvAdvice.addItemDecoration(adviceDecoration);
        binding.rvAdvice.setLayoutManager(adviceManager);
        binding.rvAdvice.setAdapter(advice2Adapter);
    }

    private void initListener() {
        //数据刷新
        binding.srlRefresh.setOnRefreshListener(refreshLayout -> startLocate());
        mainViewModel.getCitySearchResult().observe(this, location -> {
            if (location != null) {
                //更新数据
                binding.includeTemperatureNow.tvLocation.setText(location.getName());
                presenterWeather(location.getId());
            }
        });
        binding.nsvScroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (lastCpVisibleState != binding.includeAirQuality.cpvProgress.isVisibleInScreen()) {
                lastCpVisibleState = binding.includeAirQuality.cpvProgress.isVisibleInScreen();
                if (binding.includeAirQuality.cpvProgress.isVisibleInScreen()) {
                    binding.includeAirQuality.cpvProgress.startAnimator();
                }
            }
            if (lastSvVisibleState != binding.sunView.isVisibleInScreen()) {
                lastSvVisibleState = binding.sunView.isVisibleInScreen();
                if (binding.sunView.isVisibleInScreen()) {
                    binding.sunView.startAnimation();
                }
            }
            if (lastMvVisibleState != binding.moonView.isVisibleInScreen()) {
                lastMvVisibleState = binding.moonView.isVisibleInScreen();
                if (binding.moonView.isVisibleInScreen()) {
                    binding.moonView.startAnimation();
                }
            }
        });
        advice2Adapter.setOnItemClickListener((adapter, view, position) ->
                showAdvicePopupWindow(view, adviceList.get(position).getAdviceDaily().getText()));
    }

    private void showAdvicePopupWindow(View view, String content) {
        PopupWindowUtil popupWindowUtil = new PopupWindowUtil(context);
        PopupWindowAdviceBinding adviceBinding = PopupWindowAdviceBinding.inflate(LayoutInflater.from(context));
        adviceBinding.tvContent.setText(content);
        popupWindowUtil.setAutoTopPopupWindow(adviceBinding.getRoot());
        popupWindowUtil.showAsViewUp(view, DensityUtil.dp2px(10));
        new Handler().postDelayed(popupWindowUtil::closePopupWindow, 5 * 1000);
    }

    private void startLocate() {
        if (locateHelper.isGpsOpen()) {
            locateHelper.startLocate();
            binding.includeTemperatureNow.tvLocation.setText("定位中...");
        } else {
            ToastUtil.showToast("定位未打开");
            location = SPUtil.getString(Constant.LOCATION, "101010100");
            binding.includeTemperatureNow.tvLocation.setText(SPUtil.getString(Constant.DISTRICT, "北京"));
            presenterWeather(location);
        }
    }

    private void presenterWeather(String location) {
        showLoadingDialog();
        mPresenter.getWeatherNow(location);
        mPresenter.getWeatherHourly(location);
        mPresenter.getWeatherDay7(location);
        mPresenter.getAirQuality(location);
        mPresenter.getLifeAdvice(location);
        mPresenter.getSunTime(location, DateUtil.getNowDate(DateUtil.format1));
        mPresenter.getMoonTime(location, DateUtil.getNowDate(DateUtil.format1));
    }

    @Override
    public void showWeatherNow(NowResponse nowResponse) {
        binding.srlRefresh.finishRefresh();
        locateHelper.stopLocate();
        hideLoadingDialog();
        if (nowResponse.getNow() != null && nowResponse.getNow() != null) {
            if (locateHelper.isGpsOpen()) {
                binding.includeTemperatureNow.ivLocation.setVisibility(View.VISIBLE);
            } else {
                binding.includeTemperatureNow.ivLocation.setVisibility(View.GONE);
            }
            binding.includeTemperatureNow.tvTemperature.setText(nowResponse.getNow().getTemp());
            binding.includeTemperatureNow.tvDescribe.setText(nowResponse.getNow().getText());
            String time = nowResponse.getNow().getObsTime().substring(17);
            String updateTime = String.format(getResources().getString(R.string.tv_last_update_time), DateUtil.getTimeInfo(time) + time);
            binding.tvLastUpdateTime.setText(updateTime);
            String windLevel = String.format(getResources().getString(R.string.tv_wind_level), nowResponse.getNow().getWindScale());
            String windDirection = String.format(getResources().getString(R.string.tv_wind_direction), nowResponse.getNow().getWindDir());
            binding.includeWindMills.tvWindLevel.setText(windLevel);
            binding.includeWindMills.tvWindDirection.setText(windDirection);
            binding.includeWindMills.wwBig.startRotate();
            binding.includeWindMills.wwSmall.startRotate();
        }
    }

    @Override
    @SuppressLint("NotifyDataSetChanged")
    public void showWeatherHourly(HourlyResponse hourlyResponse) {
        hideLoadingDialog();
        binding.srlRefresh.finishRefresh();
        if (hourlyResponse != null && hourlyResponse.getHourly() != null) {
            hourlyList.clear();
            hourlyList.addAll(hourlyResponse.getHourly());
            animUtil.setLayoutAnimationLeft(binding.rvHourly);
            hourlyAdapter.notifyDataSetChanged();
        }
    }


    @Override
    @SuppressLint("NotifyDataSetChanged")
    public void showWeather7Day(Day7Response day7Response) {
        hideLoadingDialog();
        binding.srlRefresh.finishRefresh();
        if (day7Response != null && day7Response.getDaily() != null) {
            String temHighLow = String.format(getResources().getString(R.string.temperature_high_low),
                    day7Response.getDaily().get(0).getTempMax(),
                    day7Response.getDaily().get(0).getTempMin());
            binding.includeTemperatureNow.tvHighLow.setText(temHighLow);

            forecastList.clear();
            forecastList.addAll(day7Response.getDaily());
            animUtil.setLayoutAnimationBottom(binding.includeDailyForecast.rvDailyTop);
            topAdapter.notifyDataSetChanged();

            mPresenter.getTemperatureList(day7Response);
        }
    }

    @Override
    public void showWeatherMaxMinTemp(List<Integer> maxTempList, List<Integer> minTempList) {
        binding.includeDailyForecast.lcvMaxLine.getLineValues().clear();
        binding.includeDailyForecast.lcvMaxLine.getLineValues().addAll(maxTempList);
        binding.includeDailyForecast.lcvMaxLine.notifyLineChart();

        binding.includeDailyForecast.lcvMinLine.getLineValues().clear();
        binding.includeDailyForecast.lcvMinLine.getLineValues().addAll(minTempList);
        binding.includeDailyForecast.lcvMinLine.notifyLineChart();
    }

    @Override
    public void showAirQuality(AirQualityResponse airQualityResponse) {
        hideLoadingDialog();
        binding.srlRefresh.finishRefresh();
        if (airQualityResponse != null && airQualityResponse.getNow() != null) {
            String pm10 = TextUtils.isEmpty(airQualityResponse.getNow().getPm10()) ? "null" : airQualityResponse.getNow().getPm10();
            String pm25 = TextUtils.isEmpty(airQualityResponse.getNow().getPm2p5()) ? "null" : airQualityResponse.getNow().getPm2p5();
            String no2 = TextUtils.isEmpty(airQualityResponse.getNow().getNo2()) ? "null" : airQualityResponse.getNow().getNo2();
            String so2 = TextUtils.isEmpty(airQualityResponse.getNow().getSo2()) ? "null" : airQualityResponse.getNow().getSo2();
            String co = TextUtils.isEmpty(airQualityResponse.getNow().getCo()) ? "null" : airQualityResponse.getNow().getCo();
            String o3 = TextUtils.isEmpty(airQualityResponse.getNow().getO3()) ? "null" : airQualityResponse.getNow().getO3();
            binding.includeAirQuality.tvPm10.setText(String.format(getResources().getString(R.string.tv_pm10), pm10));
            binding.includeAirQuality.tvPm25.setText(String.format(getResources().getString(R.string.tv_pm25), pm25));
            binding.includeAirQuality.tvNo2.setText(String.format(getResources().getString(R.string.tv_no2), no2));
            binding.includeAirQuality.tvSo2.setText(String.format(getResources().getString(R.string.tv_so2), so2));
            binding.includeAirQuality.tvCo.setText(String.format(getResources().getString(R.string.tv_co), co));
            binding.includeAirQuality.tvO3.setText(String.format(getResources().getString(R.string.tv_o3), o3));
            binding.includeAirQuality.cpvProgress.setAqi(airQualityResponse.getNow().getAqi());
            binding.includeAirQuality.cpvProgress.setLevel(airQualityResponse.getNow().getCategory());
            binding.includeAirQuality.cpvProgress.setProgressColor(WeatherUtil.getAirAqiColor(Integer.parseInt(airQualityResponse.getNow().getLevel())));
        }
        binding.includeAirQuality.cpvProgress.startAnimator();
    }

    @Override
    @SuppressLint("NotifyDataSetChanged")
    public void showLiftAdvice(List<AdviceModel> adviceModelList) {
        hideLoadingDialog();
        binding.srlRefresh.finishRefresh();
        if (adviceModelList.size() > 0) {
            adviceList.clear();
            adviceList.addAll(adviceModelList);
            advice2Adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showCityId(CitySearchResponse citySearchResponse) {
        if (citySearchResponse != null) {
            binding.includeTemperatureNow.tvLocation.setText(citySearchResponse.getLocation().get(0).getName());
            String locationId = citySearchResponse.getLocation().get(0).getId();
            presenterWeather(locationId);
        }
    }

    @Override
    public void showSunRiseAndSet(SunResponse sunResponse) {
        hideLoadingDialog();
        binding.sunView.setSunTime(sunResponse.getSunrise(), sunResponse.getSunset());
        binding.sunView.startAnimation();
    }

    @Override
    public void showMoonRiseAndSet(MoonResponse moonResponse) {
        hideLoadingDialog();
        binding.moonView.setSunTime(moonResponse.getMoonrise(), moonResponse.getMoonset());
        binding.moonView.setCircleRadius(binding.sunView.getCircleRadius() - binding.moonView.getTextBound().width() - 20);
        binding.moonView.startAnimation();
    }

    @Override
    public void onFailure(String message) {
        hideLoadingDialog();
        binding.srlRefresh.finishRefresh();
        ToastUtil.showToast(message);
    }

    @Override
    public void locateResult(@NotNull BDLocation bdLocation) {
        //获取经纬度
        location = GPSUtil.doubleToString(bdLocation.getLongitude()) + "," + GPSUtil.doubleToString(bdLocation.getLatitude());
        SPUtil.putString(Constant.LOCATION, location);
        //获取区县
        String district;
        if (!bdLocation.getDistrict().isEmpty()) {
            district = bdLocation.getDistrict();
        } else if (!bdLocation.getCity().isEmpty()) {
            district = bdLocation.getCity();
        } else if (!bdLocation.getProvince().isEmpty()) {
            district = bdLocation.getProvince();
        } else {
            district = "获取位置失败";
        }
        SPUtil.putString(Constant.DISTRICT, district);
        //显示定位
        binding.includeTemperatureNow.tvLocation.setText(district);
        //获取数据
        presenterWeather(location);
        KLog.i("district is " + district + ", latitude&longitude is " + location);
    }
}