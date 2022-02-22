package com.zero.aiweather.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zero.aiweather.R;
import com.zero.aiweather.model.bean.HourlyBean;
import com.zero.aiweather.databinding.ItemWeatherHourBinding;
import com.zero.base.AIWeatherApplication;
import com.zero.base.util.WeatherUtils;

import java.util.ArrayList;
import java.util.List;

public class Hour24Adapter extends RecyclerView.Adapter<Hour24Adapter.Hour24Holder> {
    private List<HourlyBean> hourlyList = new ArrayList<>();

    public Hour24Adapter(List<HourlyBean> hourlyList) {
        this.hourlyList = hourlyList;
    }

    @NonNull
    @Override
    public Hour24Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWeatherHourBinding binding = ItemWeatherHourBinding.inflate(LayoutInflater.from(AIWeatherApplication.getContext()), parent, false);
        Hour24Holder hour24Holder = new Hour24Holder(binding);
        return hour24Holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Hour24Holder holder, int position) {
        holder.binding.tvWeatherHour.setText(hourlyList.get(position).getFxTime().substring(11, 16));
        holder.binding.tvWeatherTem.setText(hourlyList.get(position).getTemp() + "℃");
        holder.binding.ivWeatherIcon.setImageResource(WeatherUtils.getWeatherIcon(hourlyList.get(position).getText()));
    }

    @Override
    public int getItemCount() {
        return hourlyList.size();
    }

    public class Hour24Holder extends RecyclerView.ViewHolder {
        private ItemWeatherHourBinding binding;

        public Hour24Holder(@NonNull ItemWeatherHourBinding item) {
            super(item.getRoot());
            binding = item;
        }
    }
}
