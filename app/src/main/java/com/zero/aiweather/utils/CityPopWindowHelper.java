package com.zero.aiweather.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zero.aiweather.R;
import com.zero.aiweather.adapter.AreaAdapter;
import com.zero.aiweather.adapter.CityAdapter;
import com.zero.aiweather.adapter.ProvinceAdapter;
import com.zero.aiweather.application.MyApplication;
import com.zero.aiweather.databinding.PopupWindowCitiesBinding;
import com.zero.aiweather.model.ProvinceResponse;

import java.util.ArrayList;
import java.util.List;

public class CityPopWindowHelper {
    private List<ProvinceResponse> provinceList = new ArrayList<>();
    private List<ProvinceResponse.City> cityList = new ArrayList<>();
    private List<ProvinceResponse.City.Area> areaList = new ArrayList<>();
    private ProvinceAdapter provinceAdapter;
    private CityAdapter cityAdapter;
    private AreaAdapter areaAdapter;
    private String provinceTitle;

    private static CityPopWindowHelper instance;
    private ICityCallback cityCallback;

    private CityPopWindowHelper() {
    }

    public static CityPopWindowHelper newInstance(ICityCallback cityCallback) {
        if (instance == null) {
            instance = new CityPopWindowHelper();
            instance.cityCallback = cityCallback;
            instance.provinceTitle = "中国";
        }
        return instance;
    }

    public void showCityPopupWindow(List<ProvinceResponse> provinceList, Context context) {
        PopupWindowUtil popupWindowUtil = new PopupWindowUtil(context);
        PopupWindowCitiesBinding popupBinding = PopupWindowCitiesBinding.inflate(LayoutInflater.from(context));
        popupWindowUtil.showRightPopupWindow(popupBinding.getRoot());
        this.provinceList.clear();
        this.provinceList.addAll(provinceList);
        initProvinceRcv(popupBinding);
        popupWindowListener(popupBinding, popupWindowUtil);
    }

    private void initProvinceRcv(PopupWindowCitiesBinding popBinding) {
        //初始化Province RecyclerView
        provinceAdapter = new ProvinceAdapter(R.layout.item_popupwindow_city, provinceList);
        LinearLayoutManager provinceManage = new LinearLayoutManager(MyApplication.getContext());
        popBinding.rvCities.setLayoutManager(provinceManage);
        popBinding.rvCities.setAdapter(provinceAdapter);
        provinceAdapter.notifyDataSetChanged();
        RecyclerViewAnimation.runLayoutAnimationRight(popBinding.rvCities);
    }

    private void initCityRcv(PopupWindowCitiesBinding popBinding) {
        //初始化City RecyclerView
        cityAdapter = new CityAdapter(R.layout.item_popupwindow_city, cityList);
        LinearLayoutManager cityManage = new LinearLayoutManager(MyApplication.getContext());
        popBinding.rvCities.setLayoutManager(cityManage);
        popBinding.rvCities.setAdapter(cityAdapter);
        cityAdapter.notifyDataSetChanged();
        RecyclerViewAnimation.runLayoutAnimationRight(popBinding.rvCities);
    }

    private void iniAreaRcv(PopupWindowCitiesBinding popBinding) {
        //初始化县RecyclerView
        areaAdapter = new AreaAdapter(R.layout.item_popupwindow_city, areaList);
        LinearLayoutManager areaManage = new LinearLayoutManager(MyApplication.getContext());
        popBinding.rvCities.setLayoutManager(areaManage);
        popBinding.rvCities.setAdapter(areaAdapter);
        areaAdapter.notifyDataSetChanged();
        RecyclerViewAnimation.runLayoutAnimationRight(popBinding.rvCities);
    }

    private void popupWindowListener(PopupWindowCitiesBinding popBinding, PopupWindowUtil popupWindowUtil) {
        provinceAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                backToProvince(popBinding);
                //更新市级视图
                cityList.clear();
                cityList.addAll(provinceList.get(position).getCity());
                provinceTitle = provinceList.get(position).getName();
                popBinding.tvCityTitle.setText(provinceTitle);
                initCityRcv(popBinding);
                cityAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                    @Override
                    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        backToCity(popBinding);
                        //更新县级视图
                        areaList.clear();
                        areaList.addAll(cityList.get(position).getArea());
                        popBinding.tvCityTitle.setText(cityList.get(position).getName());
                        iniAreaRcv(popBinding);
                        areaAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                            @Override
                            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                                cityCallback.onItemClick(areaList.get(position).getName());
                                popupWindowUtil.closePopupWindow();
                            }
                        });
                    }
                });
            }
        });
    }

    private void backToProvince(PopupWindowCitiesBinding popBinding) {
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
    }

    private void backToCity(PopupWindowCitiesBinding popBinding) {
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
    }

    public interface ICityCallback {
        void onItemClick (String district);
    }
}
