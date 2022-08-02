package com.zero.aiweather.adapter;

import android.content.Context;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zero.aiweather.R;
import com.zero.aiweather.model.AdviceModel;

import java.util.List;

public class Advice2Adapter extends BaseQuickAdapter<AdviceModel, BaseViewHolder> {
    private Context context;

    public Advice2Adapter(int layoutResId, @Nullable List<AdviceModel> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, AdviceModel item) {
        helper.setImageDrawable(R.id.iv_item_icon, context.getDrawable(item.getIconId()));
        helper.setText(R.id.tv_item_title, item.getTitle());
        helper.setText(R.id.tv_item_level, item.getAdviceDaily().getCategory());
    }
}
