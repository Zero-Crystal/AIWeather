package com.zero.aiweather.contract;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zero.aiweather.model.bean.SearchCityBean;

public class CityViewModel extends ViewModel {
    private MutableLiveData<SearchCityBean> cityBean = new MutableLiveData<>();
    private MutableLiveData<String> inputName = new MutableLiveData<>();

    public LiveData<SearchCityBean> getCityBean() {
        return cityBean;
    }

    public LiveData<String> getInputName() {
        return inputName;
    }

    public void setCityBean(SearchCityBean cityBean) {
        this.cityBean.setValue(cityBean);
    }

    public void setInputName(String inputName) {
        this.inputName.setValue(inputName);
    }
}
