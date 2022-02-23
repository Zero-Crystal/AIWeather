package com.zero.aiweather.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.zero.aiweather.R;
import com.zero.aiweather.adapter.Day7Adapter;
import com.zero.aiweather.adapter.Hour24Adapter;
import com.zero.aiweather.model.bean.DailyBean;
import com.zero.aiweather.model.response.Day7Response;
import com.zero.aiweather.model.response.Hour24Response;
import com.zero.aiweather.model.bean.HourlyBean;
import com.zero.aiweather.contract.WeatherContract;
import com.zero.aiweather.contract.WeatherPresenter;
import com.zero.aiweather.databinding.FragmentWeatherForcastBinding;
import com.zero.aiweather.model.response.NowResponse;
import com.zero.base.AIWeatherApplication;
import com.zero.base.base.BaseFragment;
import com.zero.base.util.Constant;
import com.zero.base.util.GPSUtils;
import com.zero.base.util.LogUtils;
import com.zero.base.util.SPUtils;

import java.util.ArrayList;
import java.util.List;

public class WeatherFragment extends BaseFragment implements WeatherContract.IView {
    private FragmentWeatherForcastBinding binding;
    private WeatherPresenter presenter;
    private List<HourlyBean> hourlyList = new ArrayList<>();
    private Hour24Adapter hour24Adapter;
    private List<DailyBean> dailyList = new ArrayList<>();
    private Day7Adapter day7Adapter;
    private LocationClient locationClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWeatherForcastBinding.inflate(inflater, container, false);
        presenter = new WeatherPresenter(this, getActivity());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initLocation();
    }

    private void initView() {
        //逐小时预报
        LinearLayoutManager hourManager = new LinearLayoutManager(getContext());
        hourManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.rvWeatherHour.setLayoutManager(hourManager);
        hour24Adapter = new Hour24Adapter(hourlyList);
        binding.rvWeatherHour.setAdapter(hour24Adapter);
        //逐天预报
        LinearLayoutManager dailyManager = new LinearLayoutManager(getContext());
        dailyManager.setOrientation(RecyclerView.VERTICAL);
        binding.rvWeatherDay.setLayoutManager(dailyManager);
        day7Adapter = new Day7Adapter(dailyList);
        binding.rvWeatherDay.setAdapter(day7Adapter);
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
        if (GPSUtils.isOPen(getContext())) {
            startProgress(getContext());
            locationClient.start();
        } else {
            new AlertDialog.Builder(getContext())
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
                            binding.tvWeatherLocate.setText(SPUtils.getLocation(Constant.SP_LOCATION_DISTRICT));
                            dialogInterface.dismiss();
                        }
                    }).show();
        }
    }

    @Override
    public void showWeatherNow(NowResponse nowResponse) {
        LogUtils.d(Constant.TAG, "WeatherFragment: ----------------> showWeatherNow");
        dismissProgress();
        binding.tvWeatherTem.setText(nowResponse.getNow().getTemp() + "℃");
    }

    @Override
    public void showWeather24Hour(Hour24Response hourResponse) {
        LogUtils.d(Constant.TAG, "WeatherFragment: ----------------> showWeather24Hour");
        dismissProgress();
        hourlyList.clear();
        hourlyList.addAll(hourResponse.getHourly());
        hour24Adapter.notifyDataSetChanged();
    }

    @Override
    public void showWeather7Day(Day7Response dayResponse) {
        LogUtils.d(Constant.TAG, "WeatherFragment: ----------------> showWeather7Day");
        dismissProgress();
        binding.tvWeatherTemMaxMin.setText(dayResponse.getDaily().get(0).getTempMin() + "℃~" + dayResponse.getDaily().get(0).getTempMax() + "℃");
        binding.tvWeatherText.setText("天气" + dayResponse.getDaily().get(0).getTextDay());
        dailyList.clear();
        dailyList.addAll(dayResponse.getDaily());
        day7Adapter.notifyDataSetChanged();
    }

    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            String location = GPSUtils.doubleToString(bdLocation.getLongitude()) + "," + GPSUtils.doubleToString(bdLocation.getLatitude());
            SPUtils.saveLaLongLocation(Constant.SP_LOCATION, location);
            String district = bdLocation.getDistrict();    //获取区县
            SPUtils.saveLocation(Constant.SP_LOCATION_DISTRICT, district);
            presenter.getWeatherNow(location);
            presenter.getWeather24Hour(location);
            presenter.getWeather7Day(location);
            binding.tvWeatherLocate.setText(district);
            binding.ivLocate.setImageResource(R.drawable.locate);
            binding.ivLocate.setVisibility(View.VISIBLE);
            LogUtils.d(Constant.TAG, "WeatherFragment: ----------------> MyLocationListener: 定位方式：" + bdLocation.getLocTypeDescription());
            LogUtils.d(Constant.TAG, "WeatherFragment: ----------------> MyLocationListener: location=" + location + ", " + district);
        }
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
    public void onDestroyView() {
        super.onDestroyView();
        locationClient.stop();
    }
}