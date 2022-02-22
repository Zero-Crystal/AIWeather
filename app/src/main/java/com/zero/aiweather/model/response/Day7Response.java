package com.zero.aiweather.model.response;

import com.zero.aiweather.model.bean.DailyBean;

import java.util.List;

public class Day7Response {

    private String code;
    private String updateTime;
    private List<DailyBean> daily;

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

    public List<DailyBean> getDaily() {
        return daily;
    }

    public void setDaily(List<DailyBean> daily) {
        this.daily = daily;
    }
}
