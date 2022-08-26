package com.zero.aiweather.adapter

import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.zero.aiweather.R
import com.zero.aiweather.model.Vertical
import com.zero.aiweather.utils.DensityUtil

class WebImgAdapter(layoutResId: Int, data: MutableList<Vertical>?) :
    BaseQuickAdapter<Vertical, BaseViewHolder>(layoutResId, data) {
    var heightList = mutableListOf<Int>()

    override fun convert(helper: BaseViewHolder?, item: Vertical?) {
        val imageView = helper?.getView(R.id.iv_image) as ImageView

        val height = heightList[helper.adapterPosition].toFloat()
        val params = imageView.layoutParams
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = DensityUtil.dp2px(height)
        imageView.layoutParams = params

        if (item?.desc == "Top" || item?.desc == "Bottom") {
            Glide.with(mContext)
                .load(R.drawable.bg_img1)
                .apply(RequestOptions().transform(RoundedCorners(20)))
                .into(imageView)
        } else {
            Glide.with(mContext)
                .load(item?.img)
                .apply(RequestOptions().transform(RoundedCorners(20)))
                .into(imageView)
        }
    }
}