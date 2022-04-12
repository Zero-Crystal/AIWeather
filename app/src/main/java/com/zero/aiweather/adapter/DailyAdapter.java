package com.zero.aiweather.adapter;

import android.content.Context;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zero.aiweather.R;
import com.zero.aiweather.model.Day7Response;
import com.zero.aiweather.utils.WeatherUtil;
import com.zero.base.util.DateUtil;

import java.util.List;

public class DailyAdapter extends BaseQuickAdapter<Day7Response.Daily, BaseViewHolder> {
    private Context context;

    public DailyAdapter(int layoutResId, @Nullable List<Day7Response.Daily> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder holder, Day7Response.Daily item) {
        holder.setText(R.id.tv_item_date, DateUtil.showDateInfo(item.getFxDate()));
        String temHighLow = String.format(context.getResources().getString(R.string.temperature_high_low),
                item.getTempMax(), item.getTempMin());
        holder.setText(R.id.tv_item_tem, temHighLow);
        holder.setImageResource(R.id.iv_item_day, WeatherUtil.getWeatherIcon(item.getTextDay()));
    }
}
