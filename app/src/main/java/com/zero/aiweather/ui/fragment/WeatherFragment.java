package com.zero.aiweather.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.zero.base.base.BaseFragment;
import com.zero.base.util.Constant;
import com.zero.base.util.LogUtils;
import com.zero.base.util.WeatherUtils;

import java.util.ArrayList;
import java.util.List;

public class WeatherFragment extends BaseFragment implements WeatherContract.IView {
    private FragmentWeatherForcastBinding binding;
    private WeatherPresenter presenter;
    private List<HourlyBean> hourlyList = new ArrayList<>();
    private Hour24Adapter hour24Adapter;
    private List<DailyBean> dailyList = new ArrayList<>();
    private Day7Adapter day7Adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWeatherForcastBinding.inflate(inflater, container, false);
        presenter = new WeatherPresenter(this, getActivity());
        LogUtils.d(Constant.TAG, "WeatherFragment: ----------------> onCreateView");
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.d(Constant.TAG, "WeatherFragment: ----------------> onActivityCreated");
        initView();
        presenter.getWeatherNow();
        presenter.getWeather24Hour();
        presenter.getWeather7Day();
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

    @Override
    public void showWeatherNow(NowResponse nowResponse) {
        LogUtils.d(Constant.TAG, "WeatherFragment: ----------------> showWeatherNow");
        binding.tvWeatherTem.setText(nowResponse.getNow().getTemp() + "℃");
    }

    @Override
    public void showWeather24Hour(Hour24Response hourResponse) {
        LogUtils.d(Constant.TAG, "WeatherFragment: ----------------> showWeather24Hour");
        hourlyList.clear();
        hourlyList.addAll(hourResponse.getHourly());
        hour24Adapter.notifyDataSetChanged();
    }

    @Override
    public void showWeather7Day(Day7Response dayResponse) {
        LogUtils.d(Constant.TAG, "WeatherFragment: ----------------> showWeather7Day");
        binding.tvWeatherTemMaxMin.setText(dayResponse.getDaily().get(0).getTempMin() + "℃~" + dayResponse.getDaily().get(0).getTempMax() + "℃");
        binding.tvWeatherText.setText("天气" + dayResponse.getDaily().get(0).getTextDay());
        dailyList.clear();
        dailyList.addAll(dayResponse.getDaily());
        day7Adapter.notifyDataSetChanged();
    }
}