package com.zero.aiweather.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.zero.aiweather.R;
import com.zero.aiweather.adapter.Day7Adapter;
import com.zero.aiweather.adapter.Hour24Adapter;
import com.zero.aiweather.contract.CityViewModel;
import com.zero.aiweather.databinding.FragmentWeatherBinding;
import com.zero.aiweather.model.bean.DailyBean;
import com.zero.aiweather.model.bean.SearchCityBean;
import com.zero.aiweather.model.response.AirNowResponse;
import com.zero.aiweather.model.response.Day7Response;
import com.zero.aiweather.model.response.Hour24Response;
import com.zero.aiweather.model.bean.HourlyBean;
import com.zero.aiweather.contract.WeatherContract;
import com.zero.aiweather.contract.WeatherPresenter;
import com.zero.aiweather.model.response.NowResponse;
import com.zero.aiweather.voice.config.VoiceConfig;
import com.zero.base.AIWeatherApplication;
import com.zero.base.base.BaseFragment;
import com.zero.base.util.Constant;
import com.zero.base.util.DensityUtil;
import com.zero.base.util.GPSUtils;
import com.zero.base.util.LogUtils;
import com.zero.base.util.SPUtils;
import com.zero.base.util.ToastUtils;
import com.zero.base.util.WeatherUtils;

import java.util.ArrayList;
import java.util.List;

public class WeatherFragment extends BaseFragment implements WeatherContract.IView {
    public static final String TAG = "WeatherFragment";

    private FragmentWeatherBinding binding;
    private WeatherPresenter presenter;
    private List<HourlyBean> hourlyList = new ArrayList<>();
    private Hour24Adapter hour24Adapter;
    private List<DailyBean> dailyList = new ArrayList<>();
    private Day7Adapter day7Adapter;
    private LocationClient locationClient;
    private CityViewModel viewModel;
    private boolean isFragment = false;
    private VoiceConfig voiceConfig;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWeatherBinding.inflate(inflater, container, false);
        presenter = new WeatherPresenter(this, getActivity());
        voiceConfig = VoiceConfig.getInstance();
        voiceConfig.initAuth();
        boolean success = voiceConfig.initBdVoice();
        if (!success) {
            ToastUtils.toastShort("语音合成模块初始化失败！");
        }
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initViewModel();
        initLocation();
        isFragment = SPUtils.getStartModel(Constant.SP_START_MODEL);
        if (!isFragment) {
            LogUtils.d(TAG, "WeatherFragment: ----------------> onActivityCreated: isFragment=" + isFragment);
            startLocate();
        }
        binding.srlRefresh.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.blue));
        binding.srlRefresh.setDistanceToTriggerSync(300);
        binding.srlRefresh.setProgressViewOffset(true, 0, 100);
        binding.srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LogUtils.d(TAG, "WeatherFragment: ----------------> 下拉刷新，重新定位");
                startLocate();
            }
        });
    }

    private void initView() {
        //逐小时预报
        LinearLayoutManager hourManager = new LinearLayoutManager(getContext());
        hourManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.rvWeatherHour.setLayoutManager(hourManager);
        hour24Adapter = new Hour24Adapter(hourlyList);
        hour24Adapter.setClickListener(new Hour24Adapter.IHour24OnClickListener() {
            @Override
            public void onItemClick(HourlyBean hourly) {
                if (voiceConfig.isSpeaking()) {
                    voiceConfig.stop();
                } else {
                    voiceConfig.speak(binding.tvWeatherLocate.getText().toString() + "今日" + hourly.getFxTime().substring(11, 13) + "点，天气"
                            + hourly.getText() + "，温度" + hourly.getTemp() + "摄氏度，有"
                            + hourly.getWindDir() + "，风力" + hourly.getWindScale() + "级");
                    LogUtils.d(TAG, "WeatherFragment: ----------------> onItemClick: 逐小时天气播报成功！");

                }
            }
        });
        binding.rvWeatherHour.setAdapter(hour24Adapter);
        //逐天预报
        LinearLayoutManager dailyManager = new LinearLayoutManager(getContext());
        dailyManager.setOrientation(RecyclerView.VERTICAL);
        binding.rvWeatherDay.setLayoutManager(dailyManager);
        day7Adapter = new Day7Adapter(dailyList);
        day7Adapter.setClickListener(new Day7Adapter.IDay7OnClickListener() {
            @Override
            public void onItemClick(DailyBean daily) {
                if (voiceConfig.isSpeaking()) {
                    voiceConfig.stop();
                } else {
                    voiceConfig.speak(binding.tvWeatherLocate.getText().toString() + daily.getFxDate().substring(5, 7) + "月" + daily.getFxDate().substring(8, 10) + "日天气"
                            + daily.getTextDay() + "，最高温度" + daily.getTempMax() + "摄氏度，最低温度" + daily.getTempMin() + "摄氏度"
                            + "，有" + daily.getWindDirDay() + "，风力" + daily.getWindScaleDay() + "级");
                    LogUtils.d(TAG, "WeatherFragment: ----------------> onItemClick: 逐天天气播报成功！");
                }
            }
        });
        binding.rvWeatherDay.setAdapter(day7Adapter);
        binding.cpvProgress.setMaxNum(300);
    }

    private void initLocation() {
        locationClient = new LocationClient(AIWeatherApplication.getContext());
        LocationClientOption option = new LocationClientOption();
        //可选，是否需要地址信息
        option.setIsNeedAddress(true);
        //可选，设置是否需要最新版本的地址信息
        option.setNeedNewVersionRgc(true);
        locationClient.setLocOption(option);
        locationClient.registerLocationListener(new MyLocationListener());
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(getActivity(), new ViewModelProvider.NewInstanceFactory()).get(CityViewModel.class);
        viewModel.getCityBean().observe(this, new Observer<SearchCityBean>() {
            @Override
            public void onChanged(SearchCityBean searchCityBean) {
                LogUtils.d(TAG, "WeatherFragment: ------------------> initViewModel: 输入内容已更新，id=" + searchCityBean.getCityId()
                        + "，name=" + searchCityBean.getCityName());
                binding.tvWeatherLocate.setText(searchCityBean.getCityName());
                binding.ivLocate.setVisibility(View.INVISIBLE);
                presenter.getWeatherNow(searchCityBean.getCityId());
                presenter.getWeather24Hour(searchCityBean.getCityId());
                presenter.getWeather7Day(searchCityBean.getCityId());
                presenter.getAirNow(searchCityBean.getCityId());
                isFragment = false;
            }
        });
    }

    private void startLocate() {
        if (GPSUtils.isOPen(getContext())) {
            startProgress(getContext());
            locationClient.start();
        } else {
            showDialog();
        }
    }

    @Override
    public void showWeatherNow(NowResponse nowResponse) {
        LogUtils.d(TAG, "WeatherFragment: ----------------> showWeatherNow");
        dismissProgress();
        binding.tvWeatherTem.setText(nowResponse.getNow().getTemp() + "℃");
    }

    @Override
    public void showWeather24Hour(Hour24Response hourResponse) {
        dismissProgress();
        hourlyList.clear();
        hourlyList.addAll(hourResponse.getHourly());
        hour24Adapter.notifyDataSetChanged();
    }

    @Override
    public void showWeather7Day(Day7Response dayResponse) {
        dismissProgress();
        binding.tvWeatherTemMaxMin.setText(dayResponse.getDaily().get(0).getTempMin() + "℃~" + dayResponse.getDaily().get(0).getTempMax() + "℃");
        binding.tvWeatherText.setText("天气" + dayResponse.getDaily().get(0).getTextDay());
        dailyList.clear();
        dailyList.addAll(dayResponse.getDaily());
        day7Adapter.notifyDataSetChanged();
    }

    @Override
    public void showAirNow(AirNowResponse airNowResponse) {
        LogUtils.d(TAG, TAG + ": -----------> Aqi=" + airNowResponse.getNow().getAqi());
        binding.cpvProgress.setProgressColor(WeatherUtils.getAirAqiColor(Integer.valueOf(airNowResponse.getNow().getLevel())));
        binding.cpvProgress.setSweepAngle(Float.valueOf(airNowResponse.getNow().getAqi()));
        binding.cpvProgress.setAqi(airNowResponse.getNow().getAqi());
        binding.cpvProgress.setLevel(airNowResponse.getNow().getCategory());

        binding.tvPm10.setText("PM10：" + airNowResponse.getNow().getPm10());
        binding.tvPm25.setText("PM2.5：" + airNowResponse.getNow().getPm2p5());
        binding.tvNo2.setText("No2：" + airNowResponse.getNow().getNo2());
        binding.tvSo2.setText("So2：" + airNowResponse.getNow().getSo2());
        binding.tvCo.setText("CO：" + airNowResponse.getNow().getCo());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 887) {
            startProgress(getContext());
            locationClient.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        SPUtils.saveStartModel(Constant.SP_START_MODEL, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (locationClient.isStarted()) {
            locationClient.stop();
        }
        voiceConfig.release();
    }

    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            locationClient.stop();
            if (binding.srlRefresh.isRefreshing()) {
                binding.srlRefresh.setRefreshing(false);
            }
            String location = GPSUtils.doubleToString(bdLocation.getLongitude()) + "," + GPSUtils.doubleToString(bdLocation.getLatitude());
            SPUtils.saveLaLongLocation(Constant.SP_LOCATION, location);
            String district = bdLocation.getDistrict();    //获取区县
            SPUtils.saveLocation(Constant.SP_LOCATION_DISTRICT, district);
            presenter.getWeatherNow(location);
            presenter.getWeather24Hour(location);
            presenter.getWeather7Day(location);
            presenter.getAirNow(location);
            binding.tvWeatherLocate.setText(district);
            binding.ivLocate.setImageResource(R.drawable.icon_locate);
            binding.ivLocate.setVisibility(View.VISIBLE);
            LogUtils.d(TAG, "WeatherFragment: ----------------> MyLocationListener: 定位方式：" + bdLocation.getLocTypeDescription());
            LogUtils.d(TAG, "WeatherFragment: ----------------> MyLocationListener: location=" + location + ", " + district);
        }
    }

    private AlertDialog showDialog() {
        return new AlertDialog.Builder(getContext())
                .setTitle("当前定位未打开，是否需要开启定位？")
                .setCancelable(false)
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 887);
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startProgress(getContext());
                        String location = SPUtils.getLaLongLocation(Constant.SP_LOCATION);
                        presenter.getWeatherNow(location);
                        presenter.getWeather24Hour(location);
                        presenter.getWeather7Day(location);
                        presenter.getAirNow(location);
                        binding.tvWeatherLocate.setText(SPUtils.getLocation(Constant.SP_LOCATION_DISTRICT));
                        dialogInterface.dismiss();
                        if (binding.srlRefresh.isRefreshing()) {
                            binding.srlRefresh.setRefreshing(false);
                        }
                    }
                }).show();
    }
}