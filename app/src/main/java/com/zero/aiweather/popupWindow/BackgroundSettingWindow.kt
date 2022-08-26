package com.zero.aiweather.popupWindow

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import com.tbruyelle.rxpermissions2.RxPermissions
import com.zero.aiweather.databinding.PopupWindowBackgroundSettingBinding
import com.zero.aiweather.utils.*
import com.zero.base.util.KLog

class BackgroundSettingWindow(var context: Context) {
    var binding = PopupWindowBackgroundSettingBinding.inflate(LayoutInflater.from(context))
    var popupUtil: PopupWindowUtil = PopupWindowUtil(context)

    init {
        initRadioButton()
        initListener()
    }

    private fun initRadioButton() {
        when(SPUtil.getString(Constant.WALLPAPER_CHOOSE, Constant.DEFAULT_IMG)) {
            Constant.WEB_IMG_LIST -> binding.rbWebImg.isChecked = true
            Constant.BI_YIN_IMG -> binding.rbBiYinImg.isChecked = true
            Constant.LOCAL_IMG_LIST -> binding.rbLocalImg.isChecked = true
            Constant.PERSONAL_IMG -> binding.rbPersonalImg.isChecked = true
            else -> binding.rbDefaultImg.isChecked = true
        }
    }

    private fun initListener() {
        binding.rbWebImg.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                SPUtil.putString(Constant.WALLPAPER_CHOOSE, Constant.WEB_IMG_LIST)
                KLog.i("web image is checked")
                popupUtil.closePopupWindow()
            }
        }
        binding.rbBiYinImg.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                SPUtil.putString(Constant.WALLPAPER_CHOOSE, Constant.BI_YIN_IMG)
                KLog.i("BiYin image is checked")
                ToastUtil.showToast("背景更换成功！")
                popupUtil.closePopupWindow()
            }
        }
        binding.rbLocalImg.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                SPUtil.putString(Constant.WALLPAPER_CHOOSE, Constant.LOCAL_IMG_LIST)
                KLog.i("local image is checked")
                showLocalImgWindow()
                popupUtil.closePopupWindow()
            }
        }
        binding.rbPersonalImg.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                SPUtil.putString(Constant.WALLPAPER_CHOOSE, Constant.PERSONAL_IMG)
                KLog.i("personal image is checked")
                permissionVersion()
                popupUtil.closePopupWindow()
            }
        }
        binding.rbDefaultImg.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                SPUtil.putString(Constant.WALLPAPER_CHOOSE, Constant.DEFAULT_IMG)
                KLog.i("default image is checked")
                popupUtil.closePopupWindow()
            }
        }
    }

    /**
     * 判断版本
     */
    private fun permissionVersion() {
        val intent = Intent()
        if (Build.VERSION.SDK_INT > 23) {
            permissionRequest()
        } else {
            intent.action = Intent.ACTION_GET_CONTENT
            (context as Activity).startActivityForResult(intent, Constant.SELECT_PHOTO)
        }
    }

    /**
     * 动态申请权限
     */
    @SuppressLint("CheckResult")
    private fun permissionRequest() {
        RxPermissions(context as Activity)
            .request(Manifest.permission.READ_EXTERNAL_STORAGE)
            .subscribe { granted: Boolean ->
                if (granted) {
                    val selectPhoneIntent = CameraUtil.getSelectPhotoIntent()
                    (context as Activity).startActivityForResult(selectPhoneIntent, Constant.SELECT_PHOTO)
                    KLog.i("授权成功！")
                } else {
                    binding.rbPersonalImg.isChecked = false
                    ToastUtil.showToast("权限未开启！")
                    KLog.i("授权失败，权限未开启")
                }
            }
    }

    private fun showLocalImgWindow() {
        val localImgChoosePopup = LocalImgWindow(context)
        localImgChoosePopup.popupUtil.showBottom(localImgChoosePopup.binding.root) {
            val position = SPUtil.getInt(Constant.LIST_IMG_POSITION, -1)
            binding.rbLocalImg.isChecked = position != -1
            ToastUtil.showToast("背景更换成功！")
        }
    }

}