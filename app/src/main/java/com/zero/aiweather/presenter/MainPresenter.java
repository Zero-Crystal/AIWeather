package com.zero.aiweather.presenter;

import android.annotation.SuppressLint;

import com.zero.aiweather.application.MyApplication;
import com.zero.aiweather.contract.MainContract;
import com.zero.aiweather.model.BiYingImgResponse;
import com.zero.aiweather.model.ProvinceResponse;
import com.zero.aiweather.netApi.ApiService;
import com.zero.base.base.BasePresenter;
import com.zero.base.baseNewNet.NetWorkApi;
import com.zero.base.baseNewNet.observer.BaseObserver;
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

public class MainPresenter extends BasePresenter<MainContract.IMainView> implements MainContract.IPresenter {

    @Override
    @SuppressLint("CheckResult")
    public void getBiYinImage() {
        ApiService apiService = NetWorkApi.createService(ApiService.class, 1);
        apiService.getBiYingImage().compose(NetWorkApi.applySchedulers(new BaseObserver<BiYingImgResponse>() {
            @Override
            public void onSuccess(BiYingImgResponse biYingImgResponse) {
                if (getView() != null) {
                    getView().showBiYinImage(biYingImgResponse);
                }
            }

            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();
                if (getView() != null) {
                    getView().onFailure("数据请求失败！");
                }
                KLog.e("数据请求失败！" + e.getMessage());
            }
        }));
    }

    /**
     * 解析Province数据
     * */
    public void analiseProvinceResponse() {
        List<ProvinceResponse> provinceList = new ArrayList<>();
        try {
            //读取Cities文件
            InputStream inputStream = MyApplication.getContext().getResources().getAssets().open("City.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String lines = bufferedReader.readLine();
            while (lines != null) {
                stringBuffer.append(lines);
                lines = bufferedReader.readLine();
            }
            //解析Province数据
            final JSONArray DATA = new JSONArray(stringBuffer.toString());
            for (int i = 0; i < DATA.length(); i++) {
                JSONObject provinceObject = DATA.getJSONObject(i);
                String provinceName = provinceObject.getString("name");
                ProvinceResponse province = new ProvinceResponse();
                province.setName(provinceName);
                //解析市级数据
                List<ProvinceResponse.City> cityList = new ArrayList<>();
                JSONArray cityArray = provinceObject.getJSONArray("city");
                for (int j = 0; j < cityArray.length(); j++) {
                    JSONObject cityObject = cityArray.getJSONObject(j);
                    String cityName = cityObject.getString("name");
                    ProvinceResponse.City city = new ProvinceResponse.City();
                    city.setName(cityName);
                    //解析县级数据
                    List<ProvinceResponse.City.Area> areaList = new ArrayList<>();
                    List<String> areaNameList = new ArrayList<>();
                    JSONArray areaArray = cityObject.getJSONArray("area");
                    for (int m = 0; m < areaArray.length(); m++) {
                        areaNameList.add(areaArray.getString(m));
                    }
                    for (int n = 0; n < areaNameList.size(); n++) {
                        ProvinceResponse.City.Area area = new  ProvinceResponse.City.Area();
                        area.setName(areaNameList.get(n));
                        areaList.add(area);
                    }
                    city.setArea(areaList);
                    cityList.add(city);
                }
                province.setCity(cityList);
                provinceList.add(province);
            }
            //更新视图
            if (getView() != null) {
                getView().showCityPopWindow(provinceList);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
