package com.zero.aiweather.activity;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zero.aiweather.R;
import com.zero.aiweather.databinding.ActivitySettingsBinding;
import com.zero.aiweather.databinding.PopupWindowListImgBinding;
import com.zero.aiweather.utils.AnimationUtil;
import com.zero.aiweather.utils.CameraUtil;
import com.zero.aiweather.utils.Constant;
import com.zero.aiweather.utils.PopupWindowUtil;
import com.zero.aiweather.utils.SPUtil;
import com.zero.aiweather.utils.StatusBarUtil;
import com.zero.aiweather.utils.ToastUtil;
import com.zero.aiweather.view.SwitchButton;
import com.zero.base.base.BaseActivity;
import com.zero.base.util.KLog;

import me.shihao.library.XRadioGroup;

public class SettingsActivity extends BaseActivity {
    private ActivitySettingsBinding binding;
    private RxPermissions rxPermissions;
    private AnimationUtil animUtil;
    private float bgAlpha = 1f;//背景透明度
    private boolean bright = false;//判断标识
    private static final long DURATION = 500;//0.5s
    private static final float START_ALPHA = 0.7f;//开始透明度
    private static final float END_ALPHA = 1f;//结束透明度

    @Override
    public void initBeforeView(Bundle saveInstanceState) {

    }

    @Override
    public View getLayoutView() {
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void initDate(Bundle saveInstanceState) {
        initToolBar();
        initSwitchButton();
        initListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    String imagePath;
                    if (Build.VERSION.SDK_INT > 19) {
                        imagePath = CameraUtil.getImageOnKitKatPath(data, this);
                    } else {
                        imagePath = CameraUtil.getImageBeforeKitKatPath(data, this);
                    }
                    if (!TextUtils.isEmpty(imagePath)) {
                        SPUtil.putString(Constant.PHONE_IMG_PATH, imagePath);
                        ToastUtil.toastShort("背景更换成功！");
                    } else {
                        binding.sbImgPhone.setChecked(false);
                        ToastUtil.toastShort("背景更换失败！");
                    }
                }
                break;
            default:
                break;
        }
    }

    private void initToolBar() {
        binding.tbToolbar.setTitle("");
        setSupportActionBar(binding.tbToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        StatusBarUtil.fitTitleBar(this, binding.tbToolbar);
        Back(binding.tbToolbar);
    }

    private void initSwitchButton() {
        boolean isImgBiYing = SPUtil.getBoolean(Constant.BIYING_IMG, false);
        boolean isImgList = SPUtil.getBoolean(Constant.LIST_IMG, false);
        boolean isImgPhone = SPUtil.getBoolean(Constant.PHONE_IMG, false);
        if (isImgBiYing) {
            binding.sbImgBiYing.setChecked(true);
        } else if (isImgList) {
            binding.sbImgList.setChecked(true);
        } else if (isImgPhone) {
            binding.sbImgPhone.setChecked(true);
        }
    }

    private void initListener() {
        binding.sbImgBiYing.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    SPUtil.putBoolean(Constant.BIYING_IMG, true);
                    binding.sbImgList.setChecked(false);
                    binding.sbImgPhone.setChecked(false);
                } else {
                    SPUtil.putBoolean(Constant.BIYING_IMG, false);
                }
            }
        });
        binding.sbImgList.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    SPUtil.putBoolean(Constant.LIST_IMG, true);
                    binding.sbImgBiYing.setChecked(false);
                    binding.sbImgPhone.setChecked(false);
                    showListImgWindow();
                    toggleBright();
                } else {
                    SPUtil.putBoolean(Constant.LIST_IMG, false);
                }
            }
        });
        binding.sbImgPhone.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    SPUtil.putBoolean(Constant.PHONE_IMG, true);
                    binding.sbImgBiYing.setChecked(false);
                    binding.sbImgList.setChecked(false);
                    permissionVersion();
                } else {
                    SPUtil.putBoolean(Constant.PHONE_IMG, false);
                }
            }
        });
    }

    /**
     * 判断版本
     * */
    private void permissionVersion() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT > 23) {
            permissionRequest();
        } else {
            if (Build.VERSION.SDK_INT > 19) {
                intent.setAction(Intent.ACTION_GET_CONTENT);
            } else {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            }
            startActivityForResult(intent, Constant.SELECT_PHOTO);
        }
    }

    /**
     * 动态申请权限
     * */
    @SuppressLint("CheckResult")
    private void permissionRequest() {
        rxPermissions = new RxPermissions(SettingsActivity.this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        KLog.i("授权成功！");
                        Intent selectPhoneIntent = CameraUtil.getSelectPhotoIntent();
                        startActivityForResult(selectPhoneIntent, Constant.SELECT_PHOTO);
                    } else {
                        binding.sbImgPhone.setChecked(false);
                        ToastUtil.toastShort("权限未开启！");
                        KLog.i("授权失败，权限未开启");
                    }
                });
    }

    private void showListImgWindow() {
        animUtil = new AnimationUtil();
        PopupWindowUtil popupUtil = new PopupWindowUtil(SettingsActivity.this);
        PopupWindowListImgBinding listImgBinding = PopupWindowListImgBinding.inflate(LayoutInflater.from(SettingsActivity.this));
        int position = SPUtil.getInt(Constant.LIST_IMG_POSITION, -1);
        switch (position) {
            case 0:
                listImgBinding.rbImg1.setChecked(true);
                break;
            case 1:
                listImgBinding.rbImg2.setChecked(true);
                break;
            case 2:
                listImgBinding.rbImg3.setChecked(true);
                break;
            case 3:
                listImgBinding.rbImg4.setChecked(true);
                break;
            case 4:
                listImgBinding.rbImg5.setChecked(true);
                break;
            case 5:
                listImgBinding.rbImg6.setChecked(true);
                break;
        }
        listImgBinding.xrgListImg.setOnCheckedChangeListener(new XRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(XRadioGroup xRadioGroup, int i) {
                switch (xRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.rb_img_1:
                        SPUtil.putInt(Constant.LIST_IMG_POSITION, 0);
                        popupUtil.closePopupWindow();
                        break;
                    case R.id.rb_img_2:
                        SPUtil.putInt(Constant.LIST_IMG_POSITION, 1);
                        popupUtil.closePopupWindow();
                        break;
                    case R.id.rb_img_3:
                        SPUtil.putInt(Constant.LIST_IMG_POSITION, 2);
                        popupUtil.closePopupWindow();
                        break;
                    case R.id.rb_img_4:
                        SPUtil.putInt(Constant.LIST_IMG_POSITION, 3);
                        popupUtil.closePopupWindow();
                        break;
                    case R.id.rb_img_5:
                        SPUtil.putInt(Constant.LIST_IMG_POSITION, 4);
                        popupUtil.closePopupWindow();
                        break;
                    case R.id.rb_img_6:
                        SPUtil.putInt(Constant.LIST_IMG_POSITION, 5);
                        popupUtil.closePopupWindow();
                        break;
                    default:
                        SPUtil.putInt(Constant.LIST_IMG_POSITION, 5);
                        break;
                }
                ToastUtil.toastShort("壁纸已更换！");
            }
        });
        popupUtil.showBottomPopupWindow(listImgBinding.getRoot(), new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                toggleBright();
                int position = SPUtil.getInt(Constant.LIST_IMG_POSITION, -1);
                if (position != -1) {
                    binding.sbImgList.setChecked(true);
                } else {
                    binding.sbImgList.setChecked(false);
                }
            }
        });
    }

    /**
     * 计算动画时间
     */
    private void toggleBright() {
        // 三个参数分别为：起始值 结束值 时长，那么整个动画回调过来的值就是从0.5f--1f的
        animUtil.setValueAnimator(START_ALPHA, END_ALPHA, DURATION);
        animUtil.addUpdateListener(new AnimationUtil.UpdateListener() {
            @Override
            public void progress(float progress) {
                // 此处系统会根据上述三个值，计算每次回调的值是多少，我们根据这个值来改变透明度
                bgAlpha = bright ? progress : (START_ALPHA + END_ALPHA - progress);
                backgroundAlpha(bgAlpha);
            }
        });
        animUtil.addEndListener(new AnimationUtil.EndListener() {
            @Override
            public void endUpdate(Animator animator) {
                // 在一次动画结束的时候，翻转状态
                bright = !bright;
            }
        });
        animUtil.startAnimator();
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) context).getWindow().setAttributes(lp);
        // 此方法用来设置浮动层，防止部分手机变暗无效
        ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    public static void startSettings(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }
}