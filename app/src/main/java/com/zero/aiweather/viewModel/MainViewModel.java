package com.zero.aiweather.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.baidu.location.BDLocation;
import com.zero.aiweather.model.CitySearchResponse;

public class MainViewModel extends ViewModel {
    private MutableLiveData<String> inputLocation = new MutableLiveData<>();
    private MutableLiveData<CitySearchResponse.Location> searchResult = new MutableLiveData<>();

    public LiveData<String> getInputLocation() {
        return inputLocation;
    }

    public LiveData<CitySearchResponse.Location> getCitySearchResult() {
        return searchResult;
    }

    public void setInputLocation(String inputLocation) {
        this.inputLocation.setValue(inputLocation);
    }

    public void setSearchResult(CitySearchResponse.Location searchResult) {
        this.searchResult.setValue(searchResult);
    }

}
