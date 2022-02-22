package com.zero.aiweather.model.bean;

public class DailyBean {
    /**
     * "daily": [
     * {
     * "fxDate": "2022-02-22",
     * "sunrise": "06:59",
     * "sunset": "17:58",
     * "moonrise": "00:00",
     * "moonset": "09:50",
     * "moonPhase": "亏凸月",
     * "moonPhaseIcon": "805",
     * "tempMax": "4",
     * "tempMin": "-7",
     * "iconDay": "100",
     * "textDay": "晴",
     * "iconNight": "150",
     * "textNight": "晴",
     * "wind360Day": "225",
     * "windDirDay": "西南风",
     * "windScaleDay": "1-2",
     * "windSpeedDay": "3",
     * "wind360Night": "225",
     * "windDirNight": "西南风",
     * "windScaleNight": "1-2",
     * "windSpeedNight": "3",
     * "humidity": "44",
     * "precip": "0.0",
     * "pressure": "1029",
     * "vis": "25",
     * "cloud": "3",
     * "uvIndex": "4"
     * }
     */

    private String fxDate;
    private String tempMax;
    private String tempMin;
    private String textDay;
    private String textNight;
    private String humidity;
    private String precip;
    private String pressure;
    private String vis;
    private String cloud;
    private String uvIndex;

    public String getFxDate() {
        return fxDate;
    }

    public void setFxDate(String fxDate) {
        this.fxDate = fxDate;
    }

    public String getTempMax() {
        return tempMax;
    }

    public void setTempMax(String tempMax) {
        this.tempMax = tempMax;
    }

    public String getTempMin() {
        return tempMin;
    }

    public void setTempMin(String tempMin) {
        this.tempMin = tempMin;
    }

    public String getTextDay() {
        return textDay;
    }

    public void setTextDay(String textDay) {
        this.textDay = textDay;
    }

    public String getTextNight() {
        return textNight;
    }

    public void setTextNight(String textNight) {
        this.textNight = textNight;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPrecip() {
        return precip;
    }

    public void setPrecip(String precip) {
        this.precip = precip;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getVis() {
        return vis;
    }

    public void setVis(String vis) {
        this.vis = vis;
    }

    public String getCloud() {
        return cloud;
    }

    public void setCloud(String cloud) {
        this.cloud = cloud;
    }

    public String getUvIndex() {
        return uvIndex;
    }

    public void setUvIndex(String uvIndex) {
        this.uvIndex = uvIndex;
    }
}
