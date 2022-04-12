package com.zero.aiweather.adapter;

import android.content.Context;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zero.aiweather.R;
import com.zero.aiweather.model.AdviceResponse;

import java.util.List;

public class AdviceAdapter extends BaseQuickAdapter<AdviceResponse.Daily, BaseViewHolder> {
    private Context context;

    public AdviceAdapter(int layoutResId, @Nullable List<AdviceResponse.Daily> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, AdviceResponse.Daily item) {
        String advice = String.format(context.getResources().getString(R.string.tv_item_advice), item.getName(), item.getText());
        helper.setText(R.id.tv_item_advice, advice);
    }
}
