package com.zero.aiweather.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zero.aiweather.R;
import com.zero.aiweather.model.ProvinceResponse;

import java.util.List;

public class AreaAdapter extends BaseQuickAdapter<ProvinceResponse.City.Area, BaseViewHolder> {
    public AreaAdapter(int layoutResId, @Nullable List<ProvinceResponse.City.Area> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ProvinceResponse.City.Area item) {
        helper.setText(R.id.tv_item_city, item.getName());
        helper.addOnClickListener(R.id.tv_item_city);
    }
}
