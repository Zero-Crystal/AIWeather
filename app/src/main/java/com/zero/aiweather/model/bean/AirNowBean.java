package com.zero.aiweather.model.bean;

public class AirNowBean {
    /**
     * "now": {
     * "pubTime": "2021-02-16T14:00+08:00",
     * "aqi": "28",
     * "level": "1",
     * "category": "优",
     * "primary": "NA",
     * "pm10": "28",
     * "pm2p5": "5",
     * "no2": "3",
     * "so2": "2",
     * "co": "0.2",
     * "o3": "76"
     * }
     */

    private String pubTime;
    private String aqi;
    private String level;
    private String category;
    private String primary;
    private String pm10;
    private String pm2p5;
    private String no2;
    private String so2;
    private String co;
    private String o3;

    public String getPubTime() {
        return pubTime;
    }

    public void setPubTime(String pubTime) {
        this.pubTime = pubTime;
    }

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public String getPm10() {
        return pm10;
    }

    public void setPm10(String pm10) {
        this.pm10 = pm10;
    }

    public String getPm2p5() {
        return pm2p5;
    }

    public void setPm2p5(String pm2p5) {
        this.pm2p5 = pm2p5;
    }

    public String getNo2() {
        return no2;
    }

    public void setNo2(String no2) {
        this.no2 = no2;
    }

    public String getSo2() {
        return so2;
    }

    public void setSo2(String so2) {
        this.so2 = so2;
    }

    public String getCo() {
        return co;
    }

    public void setCo(String co) {
        this.co = co;
    }

    public String getO3() {
        return o3;
    }

    public void setO3(String o3) {
        this.o3 = o3;
    }
}
