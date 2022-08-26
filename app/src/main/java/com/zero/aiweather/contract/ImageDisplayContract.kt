package com.zero.aiweather.contract

import android.graphics.Bitmap
import com.zero.base.base.IBaseView

class ImageDisplayContract {
    interface IImageDisplayPresenter {
        /**
         * 下载图片
         * */
        fun downloadImage(imgUrl: String?)
    }

    interface IImageDisplayView: IBaseView {
        /**
         * 保存图片
         * */
        fun saveImageToGallery(bitmap: Bitmap)
    }
}