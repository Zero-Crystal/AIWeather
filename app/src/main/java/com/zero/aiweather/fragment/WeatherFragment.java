package com.zero.aiweather.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.baidu.location.BDLocation;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zero.aiweather.R;
import com.zero.aiweather.adapter.Advice2Adapter;
import com.zero.aiweather.adapter.DailyAdapter;
import com.zero.aiweather.adapter.HourlyAdapter;
import com.zero.aiweather.contract.WeatherContract;
import com.zero.aiweather.databinding.FragmentWeatherBinding;
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
import com.zero.aiweather.utils.AnimationUtil;
import com.zero.aiweather.utils.Constant;
import com.zero.aiweather.utils.GPSUtil;
import com.zero.aiweather.utils.SPUtil;
import com.zero.aiweather.utils.TimeUtil;
import com.zero.aiweather.utils.ToastUtil;
import com.zero.aiweather.utils.WeatherUtil;
import com.zero.aiweather.viewModel.SearchViewModel;
import com.zero.aiweather.widget.NormalItemDecoration;
import com.zero.base.baseMvpNet.MvpFragment;
import com.zero.base.util.DateUtil;
import com.zero.base.util.KLog;

import org.jetbrains.annotations.NotNull;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class WeatherFragment extends MvpFragment<WeatherContract.WeatherPresenter> implements WeatherContract.IWeatherView, LocationListener.LocateResult{
    private static WeatherFragment fragment;
    private FragmentWeatherBinding binding;
    private LocateHelper locateHelper;
    private List<Day7Response.Daily> forecastList = new ArrayList<>();
    private DailyAdapter forecastAdapter;
    private List<AdviceModel> adviceList = new ArrayList<>();
    private Advice2Adapter advice2Adapter;
    private List<HourlyResponse.Hourly> hourlyList = new ArrayList<>();
    private HourlyAdapter hourlyAdapter;
    private String location;
    private boolean isGpsOpen = false;
    private boolean lastCpVisibleState = false;
    private boolean lastSvVisibleState = false;
    private boolean lastMvVisibleState = false;

    private AnimationUtil animUtil;
    private SearchViewModel searchViewModel;

    public static WeatherFragment newInstance() {
        if (fragment == null) {
            fragment = new WeatherFragment();
        }
        return fragment;
    }

    @Override
    public View getLayoutView() {
        binding = FragmentWeatherBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected WeatherContract.WeatherPresenter createPresenter() {
        return new WeatherContract.WeatherPresenter();
    }

    @Override
    public void initDate(Bundle saveInstanceState) {
        animUtil = new AnimationUtil();
        searchViewModel = ViewModelProviders.of(requireActivity()).get(SearchViewModel.class);
        //初始化定位
        locateHelper = new LocateHelper(getContext());
        locateHelper.initLocate(this);
        initList();
        initListener();
        startLocate();
    }

    private void initList() {
        LinearLayoutManager forecastManager = new LinearLayoutManager(getContext());
        forecastAdapter = new DailyAdapter(R.layout.item_recyclerview_day, forecastList, getContext());
        binding.rvDaily.setLayoutManager(forecastManager);
        binding.rvDaily.setAdapter(forecastAdapter);

        LinearLayoutManager hourlyManager = new LinearLayoutManager(getContext());
        hourlyManager.setOrientation(RecyclerView.HORIZONTAL);
        hourlyAdapter = new HourlyAdapter(R.layout.item_recyclerview_hour, hourlyList);
        binding.rvHourly.setLayoutManager(hourlyManager);
        binding.rvHourly.setAdapter(hourlyAdapter);

        NormalItemDecoration itemDecoration = new NormalItemDecoration.Builder(getContext())
                .colorResId(R.color.black_90)
                .height(5)
                .create();
        GridLayoutManager adviceManager = new GridLayoutManager(getContext(), 3);
        advice2Adapter = new Advice2Adapter(R.layout.item_recyclerview_advice2, adviceList, getContext());
        binding.rvAdvice.addItemDecoration(itemDecoration);
        binding.rvAdvice.setLayoutManager(adviceManager);
        binding.rvAdvice.setAdapter(advice2Adapter);
    }

    private void initListener() {
        //数据刷新
        binding.srlRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                KLog.i("---------------------> 数据刷新");
                startLocate();
            }
        });
        searchViewModel.getCitySearchResult().observe(this, new Observer<CitySearchResponse.Location>() {
            @Override
            public void onChanged(CitySearchResponse.Location location) {
                if (location != null) {
                    Log.d("SearchResultFragment", "onChanged: <<<<<<<<<<<<<<<<<<<< result: " + location.getName() + ", " + location.getId());
                    //更新数据
                    binding.tvLocation.setText(location.getName());
                    presenterWeather(location.getId());
                }
            }
        });
        binding.nsvScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (lastCpVisibleState != binding.cpvProgress.isVisibleInScreen()) {
                    lastCpVisibleState = binding.cpvProgress.isVisibleInScreen();
                    if (binding.cpvProgress.isVisibleInScreen()) {
                        binding.cpvProgress.startAnimator();
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
            }
        });
    }

    private void startLocate() {
        isGpsOpen = locateHelper.startLocate();
        if (isGpsOpen) {
            binding.tvLocation.setText("定位中...");
        } else {
            location = SPUtil.getString(Constant.LOCATION, "101010100");
            binding.tvLocation.setText(SPUtil.getString(Constant.DISTRICT, "北京"));
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
        mPresenter.getSunTime(location, TimeUtil.getCurrentTime(TimeUtil.format3));
        mPresenter.getMoonTime(location, TimeUtil.getCurrentTime(TimeUtil.format3));
    }

    @Override
    public void showWeatherNow(NowResponse nowResponse) {
        binding.srlRefresh.finishRefresh();
        locateHelper.stopLocate();
        hideLoadingDialog();
        if (nowResponse.getNow() != null && nowResponse.getNow() != null) {
            KLog.d("------------------> 更新实时天气");
            if (isGpsOpen) {
                binding.ivLocation.setVisibility(View.VISIBLE);
            } else {
                binding.ivLocation.setVisibility(View.GONE);
            }
            binding.tvTemperature.setText(nowResponse.getNow().getTemp());
            binding.tvDescribe.setText(nowResponse.getNow().getText());
            String time = nowResponse.getNow().getObsTime().substring(17);
            String updateTime = String.format(getResources().getString(R.string.tv_last_update_time), DateUtil.showTimeInfo(time) + time);
            binding.tvLastUpdateTime.setText(updateTime);
            String windLevel = String.format(getResources().getString(R.string.tv_wind_level), nowResponse.getNow().getWindScale());
            String windDirection = String.format(getResources().getString(R.string.tv_wind_direction), nowResponse.getNow().getWindDir());
            binding.tvWindLevel.setText(windLevel);
            binding.tvWindDirection.setText(windDirection);
            binding.wwBig.startRotate();
            binding.wwSmall.startRotate();
        }
    }

    @Override
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
    public void showWeather7Day(Day7Response day7Response) {
        hideLoadingDialog();
        binding.srlRefresh.finishRefresh();
        if (day7Response != null && day7Response.getDaily() != null) {
            KLog.d("------------------> 更新7天预报");
            String temHighLow = String.format(getResources().getString(R.string.temperature_high_low),
                    day7Response.getDaily().get(0).getTempMax(), day7Response.getDaily().get(0).getTempMin());
            binding.tvHighLow.setText(temHighLow);
            forecastList.clear();
            forecastList.addAll(day7Response.getDaily());
            animUtil.setLayoutAnimationBottom(binding.rvDaily);
            forecastAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showAirQuality(AirQualityResponse airQualityResponse) {
        hideLoadingDialog();
        binding.srlRefresh.finishRefresh();
        if (airQualityResponse != null && airQualityResponse.getNow() != null) {
            KLog.d("------------------> 更新空气质量");
            String pm10 = TextUtils.isEmpty(airQualityResponse.getNow().getPm10()) ? "null" : airQualityResponse.getNow().getPm10();
            String pm25 = TextUtils.isEmpty(airQualityResponse.getNow().getPm2p5()) ? "null" : airQualityResponse.getNow().getPm2p5();
            String no2 = TextUtils.isEmpty(airQualityResponse.getNow().getNo2()) ? "null" : airQualityResponse.getNow().getNo2();
            String so2 = TextUtils.isEmpty(airQualityResponse.getNow().getSo2()) ? "null" : airQualityResponse.getNow().getSo2();
            String co = TextUtils.isEmpty(airQualityResponse.getNow().getCo()) ? "null" : airQualityResponse.getNow().getCo();
            String o3 = TextUtils.isEmpty(airQualityResponse.getNow().getO3()) ? "null" : airQualityResponse.getNow().getO3();
            binding.tvPm10.setText(String.format(getResources().getString(R.string.tv_pm10), pm10));
            binding.tvPm25.setText(String.format(getResources().getString(R.string.tv_pm25), pm25));
            binding.tvNo2.setText(String.format(getResources().getString(R.string.tv_no2), no2));
            binding.tvSo2.setText(String.format(getResources().getString(R.string.tv_so2), so2));
            binding.tvCo.setText(String.format(getResources().getString(R.string.tv_co), co));
            binding.tvO3.setText(String.format(getResources().getString(R.string.tv_o3), o3));
            binding.cpvProgress.setAqi(airQualityResponse.getNow().getAqi());
            binding.cpvProgress.setLevel(airQualityResponse.getNow().getCategory());
            binding.cpvProgress.setProgressColor(WeatherUtil.getAirAqiColor(Integer.parseInt(airQualityResponse.getNow().getLevel())));
        } else {
            binding.tvPm10.setText(String.format(getResources().getString(R.string.tv_pm10), "null"));
            binding.tvPm25.setText(String.format(getResources().getString(R.string.tv_pm25), "null"));
            binding.tvNo2.setText(String.format(getResources().getString(R.string.tv_no2), "null"));
            binding.tvSo2.setText(String.format(getResources().getString(R.string.tv_so2), "null"));
            binding.tvCo.setText(String.format(getResources().getString(R.string.tv_co), "null"));
            binding.tvO3.setText(String.format(getResources().getString(R.string.tv_o3), "null"));
            binding.cpvProgress.setAqi("0");
            binding.cpvProgress.setLevel("null");
        }
        binding.cpvProgress.startAnimator();
    }

    @Override
    public void showLiftAdvice(List<AdviceModel> adviceModelList) {
        hideLoadingDialog();
        binding.srlRefresh.finishRefresh();
        if (adviceModelList.size() > 0) {
            KLog.d("------------------> 更新生活指数");
            adviceList.clear();
            adviceList.addAll(adviceModelList);
            advice2Adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showCityId(CitySearchResponse citySearchResponse) {
        if (citySearchResponse != null) {
            isGpsOpen = false;
            String locationId = citySearchResponse.getLocation().get(0).getId();
            binding.tvLocation.setText(citySearchResponse.getLocation().get(0).getName());
            mPresenter.getWeatherNow(locationId);
            mPresenter.getWeatherHourly(locationId);
            mPresenter.getWeatherDay7(locationId);
            mPresenter.getAirQuality(locationId);
            mPresenter.getLifeAdvice(locationId);
        }
    }

    @Override
    public void showSunRiseAndSet(SunResponse sunResponse) {
        KLog.i("showMoonRiseAndSet: -------------------> sun rise time " + sunResponse.getSunrise() + ", sun set time " + sunResponse.getSunset());
        hideLoadingDialog();
        binding.sunView.setSunTime(sunResponse.getSunrise(), sunResponse.getSunset());
        binding.sunView.startAnimation();
    }

    @Override
    public void showMoonRiseAndSet(MoonResponse moonResponse) {
        KLog.i("showMoonRiseAndSet: -------------------> moon rise time " + moonResponse.getMoonrise() + ", moon set time " + moonResponse.getMoonset());
        hideLoadingDialog();
        binding.moonView.setSunTime(moonResponse.getMoonrise(), moonResponse.getMoonset());
        binding.moonView.startAnimation();
    }

    @Override
    public void onFailure(String message) {
        hideLoadingDialog();
        binding.srlRefresh.finishRefresh();
        ToastUtil.showToast(message);
    }

    @Override
    public void result(@NotNull BDLocation bdLocation) {
        //获取经纬度
        location = GPSUtil.doubleToString(bdLocation.getLongitude()) + "," + GPSUtil.doubleToString(bdLocation.getLatitude());
        SPUtil.putString(Constant.LOCATION, location);
        //获取区县
        String district = bdLocation.getDistrict();
        SPUtil.putString(Constant.DISTRICT, district);
        //显示定位
        binding.tvLocation.setText(district);
        //获取数据
        presenterWeather(location);
        KLog.d("------------------> 定位方式：" + bdLocation.getLocTypeDescription());
        KLog.d("------------------> location=" + location + ", " + district);
    }
}