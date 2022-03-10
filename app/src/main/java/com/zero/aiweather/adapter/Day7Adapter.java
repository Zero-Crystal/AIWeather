package com.zero.aiweather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zero.aiweather.R;
import com.zero.aiweather.databinding.ItemWeatherDayBinding;
import com.zero.aiweather.model.bean.DailyBean;
import com.zero.base.AIWeatherApplication;
import com.zero.base.util.WeatherUtils;

import java.util.ArrayList;
import java.util.List;

public class Day7Adapter extends RecyclerView.Adapter<Day7Adapter.Day7Holder> {
    private List<DailyBean> dailyList = new ArrayList<>();
    private IDay7OnClickListener clickListener;

    public Day7Adapter(List<DailyBean> dailyList) {
        this.dailyList = dailyList;
    }

    @NonNull
    @Override
    public Day7Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWeatherDayBinding binding = ItemWeatherDayBinding.inflate(LayoutInflater.from(AIWeatherApplication.getContext()), parent, false);
        Day7Holder holder = new Day7Holder(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Day7Holder holder, int position) {
        holder.binding.tvWeatherDate.setText(dailyList.get(position).getFxDate());
        holder.binding.tvWeatherTem.setText(dailyList.get(position).getTempMax() + "℃~" + dailyList.get(position).getTempMin() + "℃");
        holder.binding.ivWeather.setImageResource(WeatherUtils.getWeatherIcon(dailyList.get(position).getTextDay()));
        holder.binding.itemLlLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClick(dailyList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dailyList.size();
    }

    public void setClickListener(IDay7OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public class Day7Holder extends RecyclerView.ViewHolder {
        private ItemWeatherDayBinding binding;
        public Day7Holder(@NonNull ItemWeatherDayBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    public interface IDay7OnClickListener {
        void onItemClick(DailyBean daily);
    }
}
