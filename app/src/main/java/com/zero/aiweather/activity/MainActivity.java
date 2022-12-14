package com.zero.aiweather.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

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
import com.zero.aiweather.contract.MainContract;
import com.zero.aiweather.utils.CityPopWindowHelper;
import com.zero.aiweather.presenter.MainPresenter;
import com.zero.aiweather.fragment.SearchFragment;
import com.zero.aiweather.R;
import com.zero.aiweather.databinding.ActivityMainBinding;
import com.zero.aiweather.databinding.PopupWindowAddBinding;
import com.zero.aiweather.fragment.WeatherFragment;
import com.zero.aiweather.model.BiYingImgResponse;
import com.zero.aiweather.model.ProvinceResponse;
import com.zero.aiweather.utils.Constant;
import com.zero.aiweather.utils.GPSUtil;
import com.zero.aiweather.utils.ImageUtil;
import com.zero.aiweather.utils.PopupWindowUtil;
import com.zero.aiweather.utils.SPUtil;
import com.zero.aiweather.utils.StatusBarUtil;
import com.zero.aiweather.utils.ToastUtil;
import com.zero.aiweather.viewModel.MainViewModel;
import com.zero.base.baseMvp.MvpActivity;
import com.zero.base.util.KLog;

import java.util.List;

public class MainActivity
        extends MvpActivity<MainPresenter>
        implements MainContract.IMainView, IBackListener, CityPopWindowHelper.ICityCallback {
    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    public View getLayoutView() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void initDate(Bundle saveInstanceState) {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        replaceFragment(new WeatherFragment());
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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        StatusBarUtil.fitTitleBar(this, binding.tbTitle);
    }

    private void initListener() {
        binding.ivBarMap.setOnClickListener(v -> {
            if (GPSUtil.isOPen(MyApplication.getContext())) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            } else {
                ToastUtil.showToast("?????????????????????");
            }
        });
        //"+"????????????
        binding.ivBarAdd.setOnClickListener(view -> showAddPopupWindow());
        //??????????????????
        binding.tbTitle.setNavigationOnClickListener(view -> {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fl_fragment);
            if (current instanceof WeatherFragment) {
                showSearchFragment();
            } else {
                reBackWeatherFragment();
            }
        });
        //?????????????????????
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
                mainViewModel.setInputLocation(inputLocation);
                Log.d("SearchResultFragment", "afterTextChanged: >>>>>>>>>>>>>>>>>>> edit: " + inputLocation);
            }
        });
    }

    private void showSearchFragment() {
        binding.tbTitle.setNavigationIcon(R.drawable.ic_back);
        binding.llBarSearch.setVisibility(View.VISIBLE);
        binding.tvBarTitle.setVisibility(View.GONE);
        binding.ivBarAdd.setVisibility(View.GONE);
        binding.ivBarMap.setVisibility(View.GONE);
        SearchFragment fragment = new SearchFragment();
        fragment.setBackListener(this);
        replaceFragment(fragment);
    }

    private void reBackWeatherFragment() {
        binding.etBarSearch.setText("");
        binding.llBarSearch.setVisibility(View.GONE);
        binding.tbTitle.setNavigationIcon(R.drawable.ic_query);
        binding.tvBarTitle.setVisibility(View.VISIBLE);
        binding.ivBarAdd.setVisibility(View.VISIBLE);
        binding.ivBarMap.setVisibility(View.VISIBLE);
        getSupportFragmentManager().popBackStackImmediate();
    }

    public void showAddPopupWindow() {
        PopupWindowUtil popupWindowUtil = new PopupWindowUtil(context);
        PopupWindowAddBinding popAddBinding = PopupWindowAddBinding.inflate(LayoutInflater.from(context));
        popupWindowUtil.showAutoRightTop(popAddBinding.getRoot(), 10, binding.tbTitle.getHeight());
        popAddBinding.tvChangeBg.setOnClickListener(view -> {
            WallpaperActivity.Companion.startWallpaperActivity(this);
            popupWindowUtil.closePopupWindow();
        });
        popAddBinding.tvChangeCity.setOnClickListener(view -> {
            mPresenter.analiseProvinceResponse();
            popupWindowUtil.closePopupWindow();
        });
    }

    private void updateBackground() {
        String bgMethod = SPUtil.getString(Constant.WALLPAPER_CHOOSE, Constant.DEFAULT_IMG);
        switch (bgMethod) {
            case Constant.WEB_IMG_LIST:
                String imgUrl = SPUtil.getString(Constant.WEB_IMG_URL, "");
                if (!TextUtils.isEmpty(imgUrl)) {
                    loadBackground(imgUrl);
                    KLog.i("?????????????????????????????????, image url=" + imgUrl);
                }
                break;
            case Constant.BI_YIN_IMG:
                mPresenter.getBiYinImage();
                KLog.i("?????????????????????????????????");
                break;
            case Constant.LOCAL_IMG_LIST:
                int position = SPUtil.getInt(Constant.LIST_IMG_POSITION, -1);
                binding.clBg.setBackground(ImageUtil.getBackgroundDrawable(position));
                KLog.i("?????????????????????????????????????????????position=" + position);
                break;
            case Constant.PERSONAL_IMG:
                String imagePath = SPUtil.getString(Constant.PHONE_IMG_PATH,"");
                if (!TextUtils.isEmpty(imagePath)) {
                    loadBackground(imagePath);
                    KLog.i("??????????????????????????????, image path=" + imagePath);
                }
                break;
            default:
                binding.clBg.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_sun));
                KLog.i("??????????????????");
                break;
        }
    }

    private void loadBackground(String imageUrl) {
        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Drawable drawable = new BitmapDrawable(getResources(), resource);
                        binding.clBg.setBackground(drawable);
                        KLog.i("??????????????????!");
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        KLog.i("?????????????????????");
                    }
                });
    }

    private void replaceFragment(Fragment fragment) {
        //?????????????????????
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(binding.flFragment.getId(), fragment, fragment.getClass().getName());
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
            ToastUtil.showToast("??????????????????");
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
    public void onItemClickCallBack(String district) {
        WeatherFragment fragment = (WeatherFragment) getSupportFragmentManager().findFragmentByTag(WeatherFragment.class.getName());
        if (fragment != null) {
            fragment.mPresenter.getCityId(district);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBack(Fragment fragment) {
        if (fragment instanceof SearchFragment) {
            reBackWeatherFragment();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fl_fragment);
            if (current instanceof SearchFragment) {
                reBackWeatherFragment();
            } else {
                exit();
            }
        }
        return true;
    }

    public void exit() {
        if (isFastClick()) {
            MyApplication.getActivityManager().finishAll();
            System.exit(0);
        } else {
            ToastUtil.showToast("????????????????????????");
        }
    }

    public static void startMain(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}