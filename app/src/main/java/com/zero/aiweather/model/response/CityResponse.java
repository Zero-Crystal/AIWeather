package com.zero.aiweather.model.response;

import com.zero.aiweather.model.bean.LocationBean;

import java.util.List;

public class CityResponse {

    private String code;
    private List<LocationBean> location;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<LocationBean> getLocation() {
        return location;
    }

    public void setLocation(List<LocationBean> location) {
        this.location = location;
    }
}
