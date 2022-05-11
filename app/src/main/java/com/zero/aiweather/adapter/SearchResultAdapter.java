package com.zero.aiweather.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zero.aiweather.R;
import com.zero.aiweather.model.CitySearchResponse;

import java.util.List;

public class SearchResultAdapter extends BaseQuickAdapter<CitySearchResponse.Location, BaseViewHolder> {
    public SearchResultAdapter(int layoutResId, @Nullable List<CitySearchResponse.Location> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CitySearchResponse.Location item) {
        helper.setText(R.id.tv_item_search, item.getName());
    }
}
