package com.zero.aiweather.presenter

import android.graphics.BitmapFactory
import com.zero.aiweather.contract.ImageDisplayContract
import com.zero.base.base.BasePresenter
import com.zero.base.util.KLog
import okhttp3.*
import java.io.IOException

class ImageDisplayPresenter :
    BasePresenter<ImageDisplayContract.IImageDisplayView>(),
    ImageDisplayContract.IImageDisplayPresenter {

    override fun downloadImage(imgUrl: String?) {
        imgUrl?.let { url ->
            val okHttpClient = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()
            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    KLog.i("下载失败，${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    response.body()?.let {
                        try {
                            val inputStream = it.byteStream()
                            view.saveImageToGallery(BitmapFactory.decodeStream(inputStream))
                            inputStream?.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            })
        }
    }
}