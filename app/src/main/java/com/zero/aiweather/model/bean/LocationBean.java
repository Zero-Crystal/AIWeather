package com.zero.aiweather.model.bean;

public class LocationBean {
    /**
     * "name": "房山",
     * "id": "101011200",
     * "lat": "39.73553",
     * "lon": "116.13916",
     * "adm2": "北京",
     * "adm1": "北京市",
     * "country": "中国",
     */

    private String name;
    private String id;
    private String lat;
    private String lon;
    private String adm2;
    private String adm1;
    private String country;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getAdm2() {
        return adm2;
    }

    public void setAdm2(String adm2) {
        this.adm2 = adm2;
    }

    public String getAdm1() {
        return adm1;
    }

    public void setAdm1(String adm1) {
        this.adm1 = adm1;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
