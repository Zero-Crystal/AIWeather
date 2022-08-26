package com.zero.aiweather.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.zero.aiweather.R
import com.zero.aiweather.adapter.WebImgAdapter
import com.zero.aiweather.contract.WallpaperContract
import com.zero.aiweather.databinding.ActivityWallpaperBinding
import com.zero.aiweather.model.Vertical
import com.zero.aiweather.popupWindow.BackgroundSettingWindow
import com.zero.aiweather.presenter.WallpaperPresenter
import com.zero.aiweather.utils.*
import com.zero.base.baseMvp.MvpActivity
import com.zero.base.util.KLog

class WallpaperActivity :
    MvpActivity<WallpaperPresenter>(),
    WallpaperContract.IWallpaperView {
    private lateinit var binding: ActivityWallpaperBinding
    private lateinit var webImgList: MutableList<Vertical>
    private lateinit var webImgAdapter: WebImgAdapter
    private var bgSettingWindow: BackgroundSettingWindow? = null

    override fun createPresenter(): WallpaperPresenter {
        return WallpaperPresenter()
    }

    override fun getLayoutView(): View {
        binding = ActivityWallpaperBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initDate(saveInstanceState: Bundle?) {
        StatusBarUtil.transparencyBar(this)
        initList()
        initListener()
        mPresenter.getWebImgList()
    }

    private fun initListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        webImgAdapter.setOnItemClickListener { _, _, position ->
            ImageDisplayActivity.startImageViewDisplay(this, position)
            KLog.i("start image display activity")
        }
        binding.fabSetting.setOnClickListener {
            if (bgSettingWindow == null) bgSettingWindow = BackgroundSettingWindow(this)
            bgSettingWindow?.popupUtil?.showBottom(bgSettingWindow?.binding?.root, null)
        }
    }

    private fun initList() {
        webImgList = mutableListOf()
        val stageManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
        binding.rvWabImgList.layoutManager = stageManager
        webImgAdapter = WebImgAdapter(R.layout.item_recyclerview_web_img, webImgList)
        binding.rvWabImgList.adapter = webImgAdapter

        webImgAdapter.heightList.add(100)
        for (i in 1..30) {
            webImgAdapter.heightList.add(300)
        }
        webImgAdapter.heightList.add(100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constant.SELECT_PHOTO -> if (resultCode == RESULT_OK) {
                val imagePath =
                    CameraUtil.getImageOnKitKatPath(data, this)
                if (!TextUtils.isEmpty(imagePath)) {
                    SPUtil.putString(Constant.PHONE_IMG_PATH, imagePath)
                    ToastUtil.showToast("背景更换成功！")
                } else {
                    bgSettingWindow?.binding?.rbPersonalImg?.isChecked = true
                    ToastUtil.showToast("背景更换失败！")
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun showWebImgList(vertical: List<Vertical>) {
        KLog.i("web image list size is ${vertical.size}")
        webImgList.clear()
        webImgList.addAll(vertical)
        webImgAdapter.notifyDataSetChanged()
    }

    override fun onFailure(message: String) {
        ToastUtil.showToast(message)
        KLog.i(message)
    }

    companion object {
        fun startWallpaperActivity(context: Context) {
            context.startActivity(Intent(context, WallpaperActivity::class.java))
        }
    }
}