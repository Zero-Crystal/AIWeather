package com.zero.aiweather.presenter

import com.zero.aiweather.contract.WallpaperContract
import com.zero.aiweather.model.Vertical
import com.zero.aiweather.model.WebImageTable
import com.zero.aiweather.model.WebImgResponse
import com.zero.aiweather.netApi.ApiService
import com.zero.base.base.BasePresenter
import com.zero.base.baseNewNet.NetWorkApi
import com.zero.base.baseNewNet.observer.BaseObserver
import com.zero.base.util.KLog
import org.litepal.LitePal

class WallpaperPresenter :
    BasePresenter<WallpaperContract.IWallpaperView>(),
    WallpaperContract.IWallpaperPresenter {

    override fun getWebImgList() {
        NetWorkApi.createService(ApiService::class.java, 3).let { apiService ->
            apiService.webImgList.compose(
                NetWorkApi.applySchedulers(
                    object : BaseObserver<WebImgResponse>() {
                        override fun onSuccess(t: WebImgResponse?) {
                            t?.let { webImgResponse ->
                                val top = Vertical(
                                    null, null, null, "Top",
                                    null, null, "", null, null,
                                    null, null, null, null, null,
                                    null, null, null, null, null
                                )
                                val bottom = Vertical(
                                    null, null, null, "Bottom",
                                    null, null, "", null, null,
                                    null, null, null, null, null,
                                    null, null, null, null, null
                                )
                                val verticalList = arrayListOf<Vertical>()
                                verticalList.add(top)
                                verticalList.addAll(webImgResponse.res.vertical)
                                verticalList.add(bottom)
                                //存储网络图片数据
                                val imgList = arrayListOf<WebImageTable>()
                                for (vertical in verticalList) {
                                    imgList.add(WebImageTable(vertical.img))
                                }
                                LitePal.deleteAll(WebImageTable::class.java)
                                LitePal.saveAll(imgList)
                                //更新网络图片列表
                                view?.showWebImgList(verticalList)
                            }
                        }

                        override fun onFailure(e: Throwable?) {
                            e?.run {
                                printStackTrace()
                                e.message?.let { view?.onFailure(it) }
                                KLog.e("数据请求失败！$apiService")
                            }
                        }
                    })
            )
        }
    }
}