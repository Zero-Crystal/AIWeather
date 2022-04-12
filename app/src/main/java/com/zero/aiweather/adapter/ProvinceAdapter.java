package com.zero.aiweather.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zero.aiweather.R;
import com.zero.aiweather.model.CityResponse;

import java.util.List;

public class ProvinceAdapter extends BaseQuickAdapter<CityResponse, BaseViewHolder> {
    public ProvinceAdapter(int layoutResId, @Nullable List<CityResponse> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CityResponse item) {
        helper.setText(R.id.tv_item_city, item.getName());
        helper.addOnClickListener(R.id.tv_item_city);
    }
}
