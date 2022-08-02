package com.zero.aiweather.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zero.aiweather.application.MyApplication;
import com.zero.aiweather.utils.CityPopWindowHelper;
import com.zero.aiweather.contract.MainContract;
import com.zero.aiweather.fragment.SearchFragment;
import com.zero.aiweather.R;
import com.zero.aiweather.databinding.ActivityMainBinding;
import com.zero.aiweather.databinding.PopupWindowAddBinding;
import com.zero.aiweather.fragment.WeatherFragment;
import com.zero.aiweather.model.BiYingImgResponse;
import com.zero.aiweather.model.ProvinceResponse;
import com.zero.aiweather.utils.Constant;
import com.zero.aiweather.utils.ImageUtil;
import com.zero.aiweather.utils.PopupWindowUtil;
import com.zero.aiweather.utils.SPUtil;
import com.zero.aiweather.utils.StatusBarUtil;
import com.zero.aiweather.utils.ToastUtil;
import com.zero.aiweather.viewModel.SearchViewModel;
import com.zero.base.baseMvpNet.MvpActivity;
import com.zero.base.util.KLog;

import java.util.List;

public class MainActivity
        extends MvpActivity<MainContract.MainPresenter>
        implements MainContract.IMainView, IBackListener, CityPopWindowHelper.ICityCallback {
    private ActivityMainBinding binding;
    private SearchViewModel searchViewModel;
    private long exitTime = 0;

    @Override
    protected MainContract.MainPresenter createPresenter() {
        return new MainContract.MainPresenter();
    }

    @Override
    public View getLayoutView() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void initDate(Bundle saveInstanceState) {
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        replaceFragment(WeatherFragment.newInstance());
        initToolBar();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBackground();
    }

    private void initToolBar() {
        binding.tbTitle.setTitle("");
        setSupportActionBar(binding.tbTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        StatusBarUtil.fitTitleBar(this, binding.tbTitle);
    }

    private void initListener() {
        //"+"右侧弹窗
        binding.ivBarAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPopupWindow();
            }
        });
        //左上搜索图标
        binding.tbTitle.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment current = getSupportFragmentManager().findFragmentById(R.id.fl_fragment);
                if (current instanceof WeatherFragment) {
                    showSearchFragment();
                } else {
                    reBackWeatherFragment();
                }
            }
        });
        //监听编辑栏输入
        binding.etBarSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String inputLocation = editable.toString();
                searchViewModel.setInputLocation(inputLocation);
                Log.d("SearchResultFragment", "afterTextChanged: >>>>>>>>>>>>>>>>>>> edit: " + inputLocation);
            }
        });
    }

    private void showSearchFragment() {
        binding.tbTitle.setNavigationIcon(R.mipmap.icon_reuturn);
        binding.llBarSearch.setVisibility(View.VISIBLE);
        binding.tvBarTitle.setVisibility(View.GONE);
        binding.ivBarAdd.setVisibility(View.GONE);
        replaceFragment(SearchFragment.newInstance(this));
    }

    public void showAddPopupWindow() {
        PopupWindowUtil popupWindowUtil = new PopupWindowUtil(context);
        PopupWindowAddBinding popAddBinding = PopupWindowAddBinding.inflate(LayoutInflater.from(context));
        popupWindowUtil.showRightPopupWindow(popAddBinding.getRoot());
        popAddBinding.tvChangeBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsActivity.startSettings(MainActivity.this);
                popupWindowUtil.closePopupWindow();
            }
        });
        popAddBinding.tvChangeCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.analiseProvinceResponse();
                popupWindowUtil.closePopupWindow();
            }
        });
    }

    private void updateBackground() {
        boolean isImgBiYin = SPUtil.getBoolean(Constant.BIYING_IMG, false);
        boolean isImgList = SPUtil.getBoolean(Constant.LIST_IMG, false);
        boolean isImgPhone = SPUtil.getBoolean(Constant.PHONE_IMG, false);
        if (!isImgBiYin && !isImgList && !isImgPhone) {
            binding.clBg.setBackground(getResources().getDrawable(R.drawable.bg_sun));
            KLog.i("切换背景开关未打开，使用默认图片");
        } else {
            if (isImgBiYin) {
                mPresenter.getBiYingImage();
                KLog.i("切换背景，必应每日一图");
            } else if (isImgList) {
                int position = SPUtil.getInt(Constant.LIST_IMG_POSITION, -1);
                binding.clBg.setBackground(ImageUtil.getBackgroundDrawable(position));
                KLog.i("切换背景开，使用图片列表图片，position=" + position);
            } else {
                String imagePath = SPUtil.getString(Constant.PHONE_IMG_PATH,"");
                if (!TextUtils.isEmpty(imagePath)) {
                    Glide.with(this)
                            .asBitmap()
                            .load(imagePath)
                            .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            Drawable drawable = new BitmapDrawable(getResources(), resource);
                            binding.clBg.setBackground(drawable);
                            KLog.i("自定义背景切换成功");
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            KLog.i("自定义背景切换失败");
                        }
                    });
                    KLog.i("切换背景，自定义图片, image path=" + imagePath);
                }
            }
        }
    }

    private void replaceFragment(Fragment fragment) {
        //添加搜索结果页
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(binding.flFragment.getId(), fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void showBiYinImage(BiYingImgResponse biYingImgResponse) {
        hideLoadingDialog();
        if (biYingImgResponse.getImages() != null) {
            String imageUrl = "https://cn.bing.com" + biYingImgResponse.getImages().get(0).getUrl();
            Glide.with(this).asBitmap().load(imageUrl).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    Drawable drawable = new BitmapDrawable(getResources(), resource);
                    binding.clBg.setBackground(drawable);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });
        } else {
            ToastUtil.showToast("背景图片为空");
        }
    }

    @Override
    public void showCityPopWindow(List<ProvinceResponse> provinceList) {
        if (provinceList.size() > 0) {
            CityPopWindowHelper.newInstance(MainActivity.this).showCityPopupWindow(provinceList, MainActivity.this);
        }
    }

    @Override
    public void onFailure(String message) {
        hideLoadingDialog();
        ToastUtil.showToast(message);
    }

    @Override
    public void onItemClick(String district) {
        WeatherFragment.newInstance().mPresenter.getCityId(district);
    }

    @Override
    public void onBack(Fragment fragment) {
        if (fragment instanceof SearchFragment) {
            reBackWeatherFragment();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Fragment current = getSupportFragmentManager().findFragmentById(R.id.fl_fragment);
                if (current instanceof SearchFragment) {
                    reBackWeatherFragment();
                } else {
                    exit();
                }
                break;
        }
        return true;
    }

    private void reBackWeatherFragment() {
        binding.etBarSearch.setText("");
        binding.llBarSearch.setVisibility(View.GONE);
        binding.tbTitle.setNavigationIcon(R.mipmap.icon_query);
        binding.tvBarTitle.setVisibility(View.VISIBLE);
        binding.ivBarAdd.setVisibility(View.VISIBLE);
        getSupportFragmentManager().popBackStackImmediate();
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtil.showToast("再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            MyApplication.getActivityManager().finishAll();
            System.exit(0);
        }
    }

    public static void startMain(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}