package com.zero.aiweather.model.response;

import com.zero.aiweather.model.bean.NowBean;

public class NowResponse {

    private String code;
    private String updateTime;
    private NowBean now;

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

    public NowBean getNow() {
        return now;
    }

    public void setNow(NowBean now) {
        this.now = now;
    }
}
