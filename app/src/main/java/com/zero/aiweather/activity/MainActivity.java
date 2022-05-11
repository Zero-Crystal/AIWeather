package com.zero.aiweather.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zero.aiweather.fragment.SearchResultFragment;
import com.zero.aiweather.R;
import com.zero.aiweather.adapter.AdviceAdapter;
import com.zero.aiweather.adapter.AreaAdapter;
import com.zero.aiweather.adapter.CityAdapter;
import com.zero.aiweather.adapter.HourlyAdapter;
import com.zero.aiweather.adapter.ProvinceAdapter;
import com.zero.aiweather.adapter.DailyAdapter;
import com.zero.aiweather.contract.WeatherContract;
import com.zero.aiweather.databinding.ActivityMainBinding;
import com.zero.aiweather.databinding.PopupWindowAddBinding;
import com.zero.aiweather.databinding.PopupWindowCitiesBinding;
import com.zero.aiweather.model.AdviceResponse;
import com.zero.aiweather.model.AirQualityResponse;
import com.zero.aiweather.model.BiYingImgResponse;
import com.zero.aiweather.model.CityResponse;
import com.zero.aiweather.model.CitySearchResponse;
import com.zero.aiweather.model.Day7Response;
import com.zero.aiweather.model.HourlyResponse;
import com.zero.aiweather.model.NowResponse;
import com.zero.aiweather.utils.AnimationUtil;
import com.zero.aiweather.utils.Constant;
import com.zero.aiweather.utils.GPSUtil;
import com.zero.aiweather.utils.PopupWindowUtil;
import com.zero.aiweather.utils.RecyclerViewAnimation;
import com.zero.aiweather.utils.SPUtil;
import com.zero.aiweather.utils.StatusBarUtil;
import com.zero.aiweather.utils.ToastUtil;
import com.zero.aiweather.utils.WeatherUtil;
import com.zero.aiweather.viewModel.SearchViewModel;
import com.zero.base.BaseApplication;
import com.zero.base.baseMvpNet.MvpActivity;
import com.zero.base.util.DateUtil;
import com.zero.base.util.KLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends MvpActivity<WeatherContract.WeatherPresenter> implements WeatherContract.IWeatherView {
    private ActivityMainBinding binding;
    private LocationClient locationClient;
    private List<Day7Response.Daily> forecastList = new ArrayList<>();
    private DailyAdapter forecastAdapter;
    private List<AdviceResponse.Daily> adviceList = new ArrayList<>();
    private AdviceAdapter adviceAdapter;
    private List<HourlyResponse.Hourly> hourlyList = new ArrayList<>();
    private HourlyAdapter hourlyAdapter;
    private String location;
    private boolean isGpsOpen = false;
    private List<CityResponse> provinceList = new ArrayList<>();
    private List<CityResponse.City> cityList = new ArrayList<>();
    private List<CityResponse.City.Area> areaList = new ArrayList<>();
    private List<String> areaNameList = new ArrayList<>();
    private ProvinceAdapter provinceAdapter;
    private CityAdapter cityAdapter;
    private AreaAdapter areaAdapter;
    private String provinceTitle;

    private AnimationUtil animUtil;
    private float bgAlpha = 1f;//背景透明度
    private boolean bright = false;//判断标识
    private static final long DURATION = 500;//0.5s
    private static final float START_ALPHA = 0.7f;//开始透明度
    private static final float END_ALPHA = 1f;//结束透明度
    private PopupWindow mPopupWindowAdd;
    private SearchViewModel searchViewModel;

    private long exitTime = 0;

    @Override
    protected WeatherContract.WeatherPresenter createPresenter() {
        return new WeatherContract.WeatherPresenter();
    }

    @Override
    public View getLayoutView() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void initDate(Bundle saveInstanceState) {
        animUtil = new AnimationUtil();
        mPopupWindowAdd = new PopupWindow(this);
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        //添加搜索结果页
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(binding.flSearchResult.getId(), SearchResultFragment.getInstance(searchViewModel));
        ft.commit();
        initToolBar();
        initLocation();
        initList();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayBackground();
        startLocate();
    }

    private void initToolBar() {
        binding.tbTitle.setTitle("");
        setSupportActionBar(binding.tbTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        StatusBarUtil.fitTitleBar(this, binding.tbTitle);
    }

    private void initLocation() {
        locationClient = new LocationClient(BaseApplication.getContext());
        LocationClientOption option = new LocationClientOption();
        //可选，是否需要地址信息
        option.setIsNeedAddress(true);
        //可选，设置是否需要最新版本的地址信息
        option.setNeedNewVersionRgc(true);
        locationClient.setLocOption(option);
        locationClient.registerLocationListener(new MyLocationListener());
    }

    private void initList() {
        LinearLayoutManager forecastManager = new LinearLayoutManager(this);
        forecastAdapter = new DailyAdapter(R.layout.item_recyclerview_day, forecastList, this);
        binding.rvForecast.setLayoutManager(forecastManager);
        binding.rvForecast.setAdapter(forecastAdapter);

        LinearLayoutManager hourlyManager = new LinearLayoutManager(this);
        hourlyManager.setOrientation(RecyclerView.HORIZONTAL);
        hourlyAdapter = new HourlyAdapter(R.layout.item_recyclerview_hour, hourlyList);
        binding.rvHourly.setLayoutManager(hourlyManager);
        binding.rvHourly.setAdapter(hourlyAdapter);

        LinearLayoutManager adviceManager = new LinearLayoutManager(this);
        adviceAdapter = new AdviceAdapter(R.layout.item_recyclerview_advice, adviceList, this);
        binding.rvAdvice.setLayoutManager(adviceManager);
        binding.rvAdvice.setAdapter(adviceAdapter);
    }

    private void initListener() {
        //数据刷新
        binding.srlRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                KLog.i("---------------------> 数据刷新");
                startLocate();
            }
        });
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
                if (binding.flSearchResult.getVisibility() == View.VISIBLE) {
                    showWeatherPage();
                } else {
                    showSearchResultPage();
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
        searchViewModel.getCitySearchResult().observe(this, new Observer<CitySearchResponse.Location>() {
            @Override
            public void onChanged(CitySearchResponse.Location location) {
                if (location != null) {
                    Log.d("SearchResultFragment", "onChanged: <<<<<<<<<<<<<<<<<<<< result: " + location.getName() + ", " + location.getId());
                    showWeatherPage();
                    //更新数据
                    binding.tvLocation.setText(location.getName());
                    presenterWeather(location.getId());
                }
            }
        });
    }

    private void showSearchResultPage() {
        binding.tbTitle.setNavigationIcon(R.mipmap.icon_reuturn);
        //显示编辑栏，隐藏标题
        binding.llBarSearch.setVisibility(View.VISIBLE);
        binding.tvBarTitle.setVisibility(View.GONE);
        binding.ivBarAdd.setVisibility(View.GONE);
        //显示搜索结果，隐藏天气页
        binding.flSearchResult.setVisibility(View.VISIBLE);
        binding.srlRefresh.setVisibility(View.GONE);
    }

    private void showWeatherPage() {
        binding.tbTitle.setNavigationIcon(R.mipmap.icon_query);
        //清除上次输入搜索内容
        binding.etBarSearch.setText("");
        //显示标题，隐藏编辑栏
        binding.llBarSearch.setVisibility(View.GONE);
        binding.tvBarTitle.setVisibility(View.VISIBLE);
        binding.ivBarAdd.setVisibility(View.VISIBLE);
        //显示天气页，隐藏搜索结果
        binding.flSearchResult.setVisibility(View.GONE);
        binding.srlRefresh.setVisibility(View.VISIBLE);
    }

    public void showAddPopupWindow() {
        PopupWindowAddBinding popAddBinding = PopupWindowAddBinding.inflate(LayoutInflater.from(context));
        mPopupWindowAdd.setContentView(popAddBinding.getRoot());
        mPopupWindowAdd.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindowAdd.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindowAdd.setBackgroundDrawable(new ColorDrawable(0x0000));//设置pop透明效果
        mPopupWindowAdd.setOutsideTouchable(true);
        mPopupWindowAdd.setFocusable(true);
        mPopupWindowAdd.setAnimationStyle(R.style.AnimationPopupAdd);
        mPopupWindowAdd.showAsDropDown(binding.ivBarAdd, -100, 0);
        mPopupWindowAdd.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                toggleBright();
            }
        });
        popAddBinding.tvChangeBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsActivity.startSettings(MainActivity.this);
                mPopupWindowAdd.dismiss();
            }
        });
        popAddBinding.tvChangeCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCityPopupWindow();
                mPopupWindowAdd.dismiss();
            }
        });
    }

    private void showCityPopupWindow() {
        PopupWindowUtil popupWindowUtil = new PopupWindowUtil(MainActivity.this);
        PopupWindowCitiesBinding popupBinding = PopupWindowCitiesBinding.inflate(LayoutInflater.from(MainActivity.this));
        popupWindowUtil.showRightPopupWindow(popupBinding.getRoot());
        initPopupWindowData(popupBinding, popupWindowUtil);
    }

    private void initPopupWindowData(PopupWindowCitiesBinding popBinding, PopupWindowUtil popupWindowUtil) {
        try {
            //读取Cities文件
            InputStream inputStream = getResources().getAssets().open("City.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String lines = bufferedReader.readLine();
            while(lines != null) {
                stringBuffer.append(lines);
                lines = bufferedReader.readLine();
            }

            //解析Province数据
            final JSONArray DATA = new JSONArray(stringBuffer.toString());
            for (int i = 0; i < DATA.length(); i++) {
                JSONObject jsonObject = DATA.getJSONObject(i);
                String provinceName = jsonObject.getString("name");
                CityResponse response = new CityResponse();
                response.setName(provinceName);
                provinceList.add(response);
            }
            provinceAdapter = new ProvinceAdapter(R.layout.item_popupwindow_city, provinceList);
            LinearLayoutManager provinceManage = new LinearLayoutManager(MainActivity.this);
            popBinding.rvCities.setLayoutManager(provinceManage);
            popBinding.rvCities.setAdapter(provinceAdapter);
            provinceAdapter.notifyDataSetChanged();
            RecyclerViewAnimation.runLayoutAnimationRight(popBinding.rvCities);
            provinceAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    try {
                        //返回省级
                        popBinding.ivCitiesBack.setVisibility(View.VISIBLE);
                        popBinding.ivCitiesBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                popBinding.rvCities.setAdapter(provinceAdapter);
                                provinceAdapter.notifyDataSetChanged();
                                popBinding.tvCityTitle.setText("中国");
                                popBinding.ivCitiesBack.setVisibility(View.GONE);
                            }
                        });
                        //解析市级数据
                        JSONObject cityObject = DATA.getJSONObject(position);
                        popBinding.tvCityTitle.setText(provinceList.get(position).getName());
                        provinceTitle = provinceList.get(position).getName();
                        JSONArray cityArray = cityObject.getJSONArray("city");
                        if (cityList != null) {
                            cityList.clear();
                        }
                        for (int i = 0; i < cityArray.length(); i++) {
                            JSONObject areaObject = cityArray.getJSONObject(i);
                            String areaName = areaObject.getString("name");
                            CityResponse.City city = new CityResponse.City();
                            city.setName(areaName);
                            cityList.add(city);
                        }
                        //初始化City RecyclerView
                        cityAdapter = new CityAdapter(R.layout.item_popupwindow_city, cityList);
                        LinearLayoutManager cityManage = new LinearLayoutManager(MainActivity.this);
                        popBinding.rvCities.setLayoutManager(cityManage);
                        popBinding.rvCities.setAdapter(cityAdapter);
                        cityAdapter.notifyDataSetChanged();
                        RecyclerViewAnimation.runLayoutAnimationRight(popBinding.rvCities);
                        cityAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                            @Override
                            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                                try {
                                    //返回市级
                                    popBinding.ivCitiesBack.setVisibility(View.GONE);
                                    popBinding.ivAreaBack.setVisibility(View.VISIBLE);
                                    popBinding.ivAreaBack.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            popBinding.rvCities.setAdapter(cityAdapter);
                                            cityAdapter.notifyDataSetChanged();
                                            popBinding.tvCityTitle.setText(provinceTitle);
                                            popBinding.ivCitiesBack.setVisibility(View.VISIBLE);
                                            popBinding.ivAreaBack.setVisibility(View.GONE);
                                        }
                                    });
                                    //解析县级数据
                                    JSONObject areaObject = cityArray.getJSONObject(position);
                                    JSONArray areaArray = areaObject.getJSONArray("area");
                                    if (areaNameList != null) {
                                        areaNameList.clear();
                                    }
                                    if (areaList != null) {
                                        areaList.clear();
                                    }
                                    for (int i = 0; i < areaArray.length(); i++) {
                                        areaNameList.add(areaArray.getString(i));
                                    }
                                    KLog.i(areaNameList.toString());
                                    for (int j = 0; j < areaNameList.size(); j++) {
                                        CityResponse.City.Area area = new CityResponse.City.Area();
                                        area.setName(areaNameList.get(j));
                                        areaList.add(area);
                                    }
                                    //初始化县RecyclerView
                                    areaAdapter = new AreaAdapter(R.layout.item_popupwindow_city, areaList);
                                    LinearLayoutManager areaManage = new LinearLayoutManager(MainActivity.this);
                                    popBinding.rvCities.setLayoutManager(areaManage);
                                    popBinding.rvCities.setAdapter(areaAdapter);
                                    areaAdapter.notifyDataSetChanged();
                                    RecyclerViewAnimation.runLayoutAnimationRight(popBinding.rvCities);
                                    areaAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                                        @Override
                                        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                                            KLog.i("搜索 " + areaNameList.get(position));
                                            mPresenter.getCityId(areaNameList.get(position));
                                            popupWindowUtil.closePopupWindow();
                                            showLoadingDialog();
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void startLocate() {
        if (GPSUtil.isOPen(this)) {
            KLog.i("---------------------> 定位中...");
            isGpsOpen = true;
            binding.tvLocation.setText("定位中...");
            locationClient.start();
        } else {
            KLog.i("---------------------> 定位未打开");
            isGpsOpen = false;
            ToastUtil.toastShort("定位未打开");
            location = SPUtil.getString(Constant.LOCATION, "101010100");
            binding.tvLocation.setText(SPUtil.getString(Constant.DISTRICT, "北京"));
            presenterWeather(location);
        }
    }

    private void presenterWeather(String location) {
        showLoadingDialog();
        mPresenter.getWeatherNow(location);
        mPresenter.getWeatherHourly(location);
        mPresenter.getWeatherDay7(location);
        mPresenter.getAirQuality(location);
        mPresenter.getLifeAdvice(location);
    }

    private void displayBackground() {
        boolean isImgBiYing = SPUtil.getBoolean(Constant.BIYING_IMG, false);
        boolean isImgList = SPUtil.getBoolean(Constant.LIST_IMG, false);
        boolean isImgPhone = SPUtil.getBoolean(Constant.PHONE_IMG, false);
        if (!isImgBiYing && !isImgList && !isImgPhone) {
            binding.clBg.setBackground(getResources().getDrawable(R.drawable.bg_sun));
            KLog.i("切换背景开关未打开，使用默认图片");
        } else {
            if (isImgBiYing) {
                mPresenter.getBiYingImage();
                KLog.i("切换背景，必应每日一图");
            } else if (isImgList) {
                int position = SPUtil.getInt(Constant.LIST_IMG_POSITION, -1);
                switch (position) {
                    case 0:
                        binding.clBg.setBackground(getResources().getDrawable(R.drawable.bg_img1));
                        break;
                    case 1:
                        binding.clBg.setBackground(getResources().getDrawable(R.drawable.bg_img2));
                        break;
                    case 2:
                        binding.clBg.setBackground(getResources().getDrawable(R.drawable.bg_img3));
                        break;
                    case 3:
                        binding.clBg.setBackground(getResources().getDrawable(R.drawable.bg_img4));
                        break;
                    case 4:
                        binding.clBg.setBackground(getResources().getDrawable(R.drawable.bg_img5));
                        break;
                    case 5:
                        binding.clBg.setBackground(getResources().getDrawable(R.drawable.bg_img6));
                        break;
                    default:
                        binding.clBg.setBackground(getResources().getDrawable(R.drawable.bg_sun));
                        break;
                }
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

    @Override
    public void showWeatherNow(NowResponse nowResponse) {
        binding.srlRefresh.finishRefresh();
        locationClient.stop();
        hideLoadingDialog();
        if (nowResponse.getNow() != null && nowResponse.getNow() != null) {
            KLog.d("------------------> 更新实时天气");
            if (isGpsOpen) {
                binding.ivLocation.setVisibility(View.VISIBLE);
            } else {
                binding.ivLocation.setVisibility(View.GONE);
            }
            binding.tvTemperature.setText(nowResponse.getNow().getTemp());
            binding.tvDescribe.setText(nowResponse.getNow().getText());
            String time = nowResponse.getNow().getObsTime().substring(17);
            String updateTime = String.format(getResources().getString(R.string.tv_last_update_time), DateUtil.showTimeInfo(time) + time);
            binding.tvLastUpdateTime.setText(updateTime);
            String windLevel = String.format(getResources().getString(R.string.tv_wind_level), nowResponse.getNow().getWindScale());
            String windDirection = String.format(getResources().getString(R.string.tv_wind_direction), nowResponse.getNow().getWindDir());
            binding.tvWindLevel.setText(windLevel);
            binding.tvWindDirection.setText(windDirection);
            binding.wwBig.startRotate();
            binding.wwSmall.startRotate();
        }
    }

    @Override
    public void showWeatherHourly(HourlyResponse hourlyResponse) {
        hideLoadingDialog();
        binding.srlRefresh.finishRefresh();
        if (hourlyResponse != null && hourlyResponse.getHourly() != null) {
            hourlyList.clear();
            hourlyList.addAll(hourlyResponse.getHourly());
            hourlyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showWeather7Day(Day7Response day7Response) {
        hideLoadingDialog();
        binding.srlRefresh.finishRefresh();
        if (day7Response != null && day7Response.getDaily() != null) {
            KLog.d("------------------> 更新7天预报");
            String temHighLow = String.format(getResources().getString(R.string.temperature_high_low),
                    day7Response.getDaily().get(0).getTempMax(), day7Response.getDaily().get(0).getTempMin());
            binding.tvHighLow.setText(temHighLow);
            forecastList.clear();
            forecastList.addAll(day7Response.getDaily());
            forecastAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showLiftAdvice(AdviceResponse adviceResponse) {
        hideLoadingDialog();
        binding.srlRefresh.finishRefresh();
        if (adviceResponse != null && adviceResponse.getDaily() != null) {
            KLog.d("------------------> 更新生活指数");
            adviceList.clear();
            adviceList.addAll(adviceResponse.getDaily());
            adviceAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showCityId(CitySearchResponse citySearchResponse) {
        if (citySearchResponse != null) {
            isGpsOpen = false;
            String locationId = citySearchResponse.getLocation().get(0).getId();
            binding.tvLocation.setText(citySearchResponse.getLocation().get(0).getName());
            mPresenter.getBiYingImage();
            mPresenter.getWeatherNow(locationId);
            mPresenter.getWeatherHourly(locationId);
            mPresenter.getWeatherDay7(locationId);
            mPresenter.getAirQuality(locationId);
            mPresenter.getLifeAdvice(locationId);
        }
    }

    @Override
    public void showBiYingImage(BiYingImgResponse biYingImgResponse) {
        hideLoadingDialog();
        binding.srlRefresh.finishRefresh();
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
            ToastUtil.toastShort("背景图片为空");
        }
    }

    @Override
    public void showAirQuality(AirQualityResponse airQualityResponse) {
        hideLoadingDialog();
        binding.srlRefresh.finishRefresh();
        if (airQualityResponse != null && airQualityResponse.getNow() != null) {
            KLog.d("------------------> 更新空气质量");
            String pm10 = TextUtils.isEmpty(airQualityResponse.getNow().getPm10()) ? " " : airQualityResponse.getNow().getPm10();
            String pm25 = TextUtils.isEmpty(airQualityResponse.getNow().getPm2p5()) ? " " : airQualityResponse.getNow().getPm2p5();
            String no2 = TextUtils.isEmpty(airQualityResponse.getNow().getNo2()) ? " " : airQualityResponse.getNow().getNo2();
            String so2 = TextUtils.isEmpty(airQualityResponse.getNow().getSo2()) ? " " : airQualityResponse.getNow().getSo2();
            String co = TextUtils.isEmpty(airQualityResponse.getNow().getCo()) ? " " : airQualityResponse.getNow().getCo();
            String o3 = TextUtils.isEmpty(airQualityResponse.getNow().getO3()) ? " " : airQualityResponse.getNow().getO3();
            binding.tvPm10.setText(String.format(getResources().getString(R.string.tv_pm10), pm10));
            binding.tvPm25.setText(String.format(getResources().getString(R.string.tv_pm25), pm25));
            binding.tvNo2.setText(String.format(getResources().getString(R.string.tv_no2), no2));
            binding.tvSo2.setText(String.format(getResources().getString(R.string.tv_so2), so2));
            binding.tvCo.setText(String.format(getResources().getString(R.string.tv_co), co));
            binding.tvO3.setText(String.format(getResources().getString(R.string.tv_o3), o3));
            binding.cpvProgress.setAqi(airQualityResponse.getNow().getAqi());
            binding.cpvProgress.setLevel(airQualityResponse.getNow().getCategory());
            binding.cpvProgress.setProgressColor(WeatherUtil.getAirAqiColor(Integer.parseInt(airQualityResponse.getNow().getLevel())));
        } else {
            binding.tvPm10.setText(String.format(getResources().getString(R.string.tv_pm10), "null"));
            binding.tvPm25.setText(String.format(getResources().getString(R.string.tv_pm25), "null"));
            binding.tvNo2.setText(String.format(getResources().getString(R.string.tv_no2), "null"));
            binding.tvSo2.setText(String.format(getResources().getString(R.string.tv_so2), "null"));
            binding.tvCo.setText(String.format(getResources().getString(R.string.tv_co), "null"));
            binding.tvO3.setText(String.format(getResources().getString(R.string.tv_o3), "null"));
            binding.cpvProgress.setAqi("0");
            binding.cpvProgress.setLevel("null");
        }
        binding.cpvProgress.startAnimator();
    }

    @Override
    public void onFailure(String message) {
        hideLoadingDialog();
        binding.srlRefresh.finishRefresh();
        ToastUtil.toastShort(message);
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

    public static void startMain(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    private class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //获取经纬度
            location = GPSUtil.doubleToString(bdLocation.getLongitude()) + "," + GPSUtil.doubleToString(bdLocation.getLatitude());
            SPUtil.putString(Constant.LOCATION, location);
            //获取区县
            String district = bdLocation.getDistrict();
            SPUtil.putString(Constant.DISTRICT, district);
            //显示定位
            binding.tvLocation.setText(district);
            //获取数据
            presenterWeather(location);
            KLog.d("------------------> 定位方式：" + bdLocation.getLocTypeDescription());
            KLog.d("------------------> location=" + location + ", " + district);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (binding.flSearchResult.getVisibility() == View.VISIBLE) {
                    showWeatherPage();
                } else {
                    exit();
                }
                break;
        }
        return true;
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtil.toastShort("再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

}