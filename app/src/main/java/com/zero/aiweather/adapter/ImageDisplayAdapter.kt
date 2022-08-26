package com.zero.aiweather.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.zero.aiweather.R
import com.zero.aiweather.model.WebImageTable

class ImageDisplayAdapter(layoutResId: Int, data: MutableList<WebImageTable>?) :
    BaseQuickAdapter<WebImageTable, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder?, item: WebImageTable?) {
        val imageView = helper?.getView<ImageView>(R.id.iv_display)
        imageView?.let {
            Glide.with(mContext)
                .load(item?.imageUrl)
                .into(it)
        }
    }
}