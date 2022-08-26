package com.zero.aiweather.contract

import com.zero.aiweather.model.Vertical
import com.zero.aiweather.model.WebImgResponse
import com.zero.base.base.IBaseView

class WallpaperContract {
    interface IWallpaperPresenter{
        /**
         * 获取壁纸列表
         * */
        fun getWebImgList()
    }

    interface IWallpaperView: IBaseView {
        /**
         * 展示壁纸列表
         * */
        fun showWebImgList(vertical: List<Vertical>)
        /**
         * 请求失败
         * */
        fun onFailure(message: String)
    }
}