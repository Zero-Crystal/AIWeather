package com.zero.base.net;

/**
 * api接口集合
 * */
public class NetApi {
    /**
     * 查询城市
     * */
    public static final String chinaCities = "https://geoapi.qweather.com/v2/city/lookup?";

    public static final String BaseUrl = "https://devapi.qweather.com/v7/weather";

    /**
     * 实时天气
     * */
    public static final String weatherNow = "/now";

    /**
     * 逐小时预报
     * */
    public static final String weather24Hour = "/24h";

    /**
     * 7天天气预报
     * */
    public static final String weather7Day = "/7d";
}
