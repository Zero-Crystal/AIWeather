package com.zero.aiweather.popupWindow

import android.content.Context
import android.view.LayoutInflater
import com.zero.aiweather.R
import com.zero.aiweather.databinding.PopupWindowListImgBinding
import com.zero.aiweather.utils.*

class LocalImgWindow(var context: Context){
    var binding = PopupWindowListImgBinding.inflate(LayoutInflater.from(context))
    var popupUtil: PopupWindowUtil = PopupWindowUtil(context)

    init {
        initPopupWindowView()
        initListener()
    }

    private fun initListener() {
        binding.xrgListImg.setOnCheckedChangeListener { xRadioGroup, _ ->
            when (xRadioGroup.checkedRadioButtonId) {
                R.id.rb_img_1 -> {
                    SPUtil.putInt(Constant.LIST_IMG_POSITION, 0)
                    popupUtil.closePopupWindow()
                }
                R.id.rb_img_2 -> {
                    SPUtil.putInt(Constant.LIST_IMG_POSITION, 1)
                    popupUtil.closePopupWindow()
                }
                R.id.rb_img_3 -> {
                    SPUtil.putInt(Constant.LIST_IMG_POSITION, 2)
                    popupUtil.closePopupWindow()
                }
                R.id.rb_img_4 -> {
                    SPUtil.putInt(Constant.LIST_IMG_POSITION, 3)
                    popupUtil.closePopupWindow()
                }
                R.id.rb_img_5 -> {
                    SPUtil.putInt(Constant.LIST_IMG_POSITION, 4)
                    popupUtil.closePopupWindow()
                }
                R.id.rb_img_6 -> {
                    SPUtil.putInt(Constant.LIST_IMG_POSITION, 5)
                    popupUtil.closePopupWindow()
                }
                else -> SPUtil.putInt(Constant.LIST_IMG_POSITION, 5)
            }
            ToastUtil.showToast("壁纸已更换！")
        }
    }

    private fun initPopupWindowView() {
        when (SPUtil.getInt(Constant.LIST_IMG_POSITION, -1)) {
            0 -> binding.rbImg1.isChecked = true
            1 -> binding.rbImg2.isChecked = true
            2 -> binding.rbImg3.isChecked = true
            3 -> binding.rbImg4.isChecked = true
            4 -> binding.rbImg5.isChecked = true
            5 -> binding.rbImg6.isChecked = true
        }
    }
}