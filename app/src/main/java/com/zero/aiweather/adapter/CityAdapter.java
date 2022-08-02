package com.zero.aiweather.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zero.aiweather.R;
import com.zero.aiweather.model.ProvinceResponse;

import java.util.List;

public class CityAdapter extends BaseQuickAdapter<ProvinceResponse.City, BaseViewHolder> {
    public CityAdapter(int layoutResId, @Nullable List<ProvinceResponse.City> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ProvinceResponse.City item) {
        helper.setText(R.id.tv_item_city, item.getName());
        helper.addOnClickListener(R.id.tv_item_city);
    }
}
