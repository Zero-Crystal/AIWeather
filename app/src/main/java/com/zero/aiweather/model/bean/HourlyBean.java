package com.zero.aiweather.model.bean;

public class HourlyBean {
    /**
     * "hourly": [
     * {
     * "fxTime": "2021-02-16T15:00+08:00",
     * "temp": "2",
     * "icon": "100",
     * "text": "晴",
     * "wind360": "335",
     * "windDir": "西北风",
     * "windScale": "3-4",
     * "windSpeed": "20",
     * "humidity": "11",
     * "pop": "0",
     * "precip": "0.0",
     * "pressure": "1025",
     * "cloud": "0",
     * "dew": "-25"
     * }
     */

    private String fxTime;
    private String temp;
    private String text;
    private String windDir;
    private String windScale;
    private String windSpeed;
    private String humidity;

    public String getFxTime() {
        return fxTime;
    }

    public void setFxTime(String fxTime) {
        this.fxTime = fxTime;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
