package com.zero.aiweather.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.zero.aiweather.R
import com.zero.aiweather.adapter.ImageDisplayAdapter
import com.zero.aiweather.contract.ImageDisplayContract
import com.zero.aiweather.databinding.ActivityImageDisplayBinding
import com.zero.aiweather.model.WebImageTable
import com.zero.aiweather.presenter.ImageDisplayPresenter
import com.zero.aiweather.utils.*
import com.zero.base.baseMvp.MvpActivity
import com.zero.base.util.KLog
import org.litepal.LitePal

class ImageDisplayActivity : MvpActivity<ImageDisplayPresenter>(),
    ImageDisplayContract.IImageDisplayView {
    private lateinit var binding: ActivityImageDisplayBinding
    private lateinit var list: MutableList<WebImageTable>
    private var imgPosition = 0

    override fun createPresenter(): ImageDisplayPresenter {
        return ImageDisplayPresenter()
    }

    override fun getLayoutView(): View {
        binding = ActivityImageDisplayBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initDate(saveInstanceState: Bundle?) {
        initStatusBar()
        initView()
        initListener()
    }

    private fun initStatusBar() {
        StatusBarUtil.transparencyBar(this)
        StatusBarUtil.StatusBarLightMode(this)
    }

    private fun initView() {
        imgPosition = intent.getIntExtra(IMG_POSITION_KEY, 0)
        list = LitePal.findAll(WebImageTable::class.java)
        val imgAdapter = ImageDisplayAdapter(R.layout.item_recyclerview_imageview, list)
        binding.vpImageDisplay.adapter = imgAdapter
        binding.vpImageDisplay.setCurrentItem(imgPosition, true)
    }

    private fun initListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.vpImageDisplay.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    imgPosition = position
                }
            })
        binding.tvSetting.setOnClickListener {
            if (imgPosition == 0 || imgPosition == 31) return@setOnClickListener

            SPUtil.putString(Constant.WALLPAPER_CHOOSE, Constant.WEB_IMG_LIST)
            SPUtil.putString(Constant.WEB_IMG_URL, list[imgPosition].imageUrl)
            ToastUtil.showToast("设置成功！")
            KLog.i("image url save success!")
        }
        binding.tvDownload.setOnClickListener {
            val url = SPUtil.getString(Constant.WEB_IMG_URL, null)
            mPresenter.downloadImage(url)
        }
    }

    override fun saveImageToGallery(bitmap: Bitmap) {
        runOnUiThread {
            ImageUtil.saveImageToGallery(bitmap)
        }
    }

    companion object {
        const val IMG_POSITION_KEY = "ImagePosition"
        fun startImageViewDisplay(context: Context, imgId: Int) {
            val intent = Intent(context, ImageDisplayActivity::class.java)
            intent.putExtra(IMG_POSITION_KEY, imgId)
            context.startActivity(intent)
        }
    }
}