package com.zero.aiweather.model.response;

import com.zero.aiweather.model.bean.HourlyBean;

import java.util.List;

public class Hour24Response {

    private String code;
    private String updateTime;
    private List<HourlyBean> hourly;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public List<HourlyBean> getHourly() {
        return hourly;
    }

    public void setHourly(List<HourlyBean> hourly) {
        this.hourly = hourly;
    }
}
