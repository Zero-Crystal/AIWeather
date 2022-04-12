package com.zero.aiweather.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zero.aiweather.R;
import com.zero.aiweather.model.HourlyResponse;
import com.zero.aiweather.utils.WeatherUtil;
import com.zero.base.util.DateUtil;

import java.util.List;

public class HourlyAdapter extends BaseQuickAdapter<HourlyResponse.Hourly, BaseViewHolder> {

    public HourlyAdapter(int layoutResId, @Nullable List<HourlyResponse.Hourly> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, HourlyResponse.Hourly item) {
        String time = item.getFxTime().substring(11, 16);
        helper.setText(R.id.tv_item_hour, DateUtil.showTimeInfo(time) + time);
        helper.setText(R.id.tv_item_tem, item.getTemp());
        helper.setImageResource(R.id.iv_item_icon, WeatherUtil.getWeatherIcon(item.getText()));
    }
}
