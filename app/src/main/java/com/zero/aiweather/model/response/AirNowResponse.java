package com.zero.aiweather.model.response;

import com.zero.aiweather.model.bean.AirNowBean;
import com.zero.aiweather.model.bean.AirStationBean;

import java.util.List;

public class AirNowResponse {
    private String code;
    private String updateTime;
    private String fxLink;
    private AirNowBean now;
    private List<AirStationBean> station;

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

    public String getFxLink() {
        return fxLink;
    }

    public void setFxLink(String fxLink) {
        this.fxLink = fxLink;
    }

    public AirNowBean getNow() {
        return now;
    }

    public void setNow(AirNowBean now) {
        this.now = now;
    }

    public List<AirStationBean> getStation() {
        return station;
    }

    public void setStation(List<AirStationBean> station) {
        this.station = station;
    }
}
