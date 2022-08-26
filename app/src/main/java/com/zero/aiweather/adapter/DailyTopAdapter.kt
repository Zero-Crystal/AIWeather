package com.zero.aiweather.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.zero.aiweather.R
import com.zero.aiweather.model.Day7Response
import com.zero.aiweather.utils.WeatherUtil
import com.zero.base.util.DateUtil

class DailyTopAdapter: BaseQuickAdapter<Day7Response.Daily, BaseViewHolder> {

    constructor(layoutResId: Int, data: MutableList<Day7Response.Daily>?) : super(layoutResId, data)

    override fun convert(helper: BaseViewHolder?, item: Day7Response.Daily?) {
        helper?.apply {
            setText(R.id.tv_item_week, DateUtil.Week(item?.fxDate))
            setText(R.id.tv_item_date, item?.fxDate?.substring(5))
            setImageResource(R.id.iv_item_icon, WeatherUtil.getWeatherIcon(item?.textDay))
            setText(R.id.tv_item_describe, item?.textDay)
        }
    }
}