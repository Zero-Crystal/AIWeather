package com.zero.aiweather.model.bean;

public class NowBean {
    /**
     * "now": {
     * "obsTime": "2022-02-22T14:10+08:00",
     * "temp": "3",
     * "feelsLike": "-3",
     * "icon": "100",
     * "text": "晴",
     * "wind360": "270",
     * "windDir": "西风",
     * "windScale": "4",
     * "windSpeed": "20",
     * "humidity": "17",
     * "precip": "0.0",
     * "pressure": "1032",
     * "vis": "30",
     * "cloud": "0",
     * "dew": "-23"
     * }
     */

    private String obsTime;
    private String temp;
    private String feelsLike;
    private String text;
    private String wind360;
    private String windDir;
    private String windScale;
    private String windSpeed;
    private String humidity;

    public String getObsTime() {
        return obsTime;
    }

    public void setObsTime(String obsTime) {
        this.obsTime = obsTime;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(String feelsLike) {
        this.feelsLike = feelsLike;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getWind360() {
        return wind360;
    }

    public void setWind360(String wind360) {
        this.wind360 = wind360;
    }

    public String getWindDir() {
        return windDir;
    }

    public void setWindDir(String windDir) {
        this.windDir = windDir;
    }

    public String getWindScale() {
        return windScale;
    }

    public void setWindScale(String windScale) {
        this.windScale = windScale;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }
}
