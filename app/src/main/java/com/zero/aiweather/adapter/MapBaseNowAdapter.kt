package com.zero.aiweather.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.zero.aiweather.R
import com.zero.aiweather.model.MapBaseModel

class MapBaseNowAdapter(
        layoutResId: Int,
        list: List<MapBaseModel>
): BaseQuickAdapter<MapBaseModel, BaseViewHolder>(layoutResId, list) {
    override fun convert(helper: BaseViewHolder?, item: MapBaseModel?) {
        helper?.run {
            item?.let {
                setImageResource(R.id.iv_item_icon, it.iconId)
                setText(R.id.tv_item_title, it.title)
                setText(R.id.tv_item_number, it.number)
            }
        }
    }
}