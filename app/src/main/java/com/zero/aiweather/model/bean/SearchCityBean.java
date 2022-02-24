package com.zero.aiweather.model.bean;

public class SearchCityBean {
    private String cityId;
    private String cityName;

    public SearchCityBean() {
    }

    public SearchCityBean(String cityId, String cityName) {
        this.cityId = cityId;
        this.cityName = cityName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
