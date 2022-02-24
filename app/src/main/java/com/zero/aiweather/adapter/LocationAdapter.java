package com.zero.aiweather.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.zero.aiweather.R;
import com.zero.aiweather.databinding.ItemLocationSearchBinding;
import com.zero.aiweather.model.bean.LocationBean;
import com.zero.base.util.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocationAdapter extends BaseAdapter {
    private List<LocationBean> locationList;
    private Context context;
    private String inputText;

    public LocationAdapter(List<LocationBean> locationList, Context context) {
        this.locationList = locationList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return locationList.size();
    }

    @Override
    public Object getItem(int i) {
        return locationList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ItemLocationSearchBinding binding = ItemLocationSearchBinding.inflate(LayoutInflater.from(context), viewGroup, false);
        if (!TextUtils.isEmpty(inputText)) {
            SpannableString city = matchInput(context.getColor(R.color.purple_200), locationList.get(i).getName(), inputText);
            binding.tvItemLocation.setText(city);
        } else {
            binding.tvItemLocation.setText(locationList.get(i).getName());
        }
        return binding.getRoot();
    }

    private SpannableString matchInput(int color, String text, String input) {
        SpannableString string = new SpannableString(text);
        Pattern key = Pattern.compile(input);
        Matcher matcher = key.matcher(string);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            string.setSpan(new ForegroundColorSpan(color), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return string;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }
}
