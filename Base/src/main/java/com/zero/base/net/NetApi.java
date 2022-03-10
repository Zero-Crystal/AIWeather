package com.zero.base.net;

/**
 * api接口集合
 * */
public class NetApi {
    /**
     * 查询城市
     * */
    public static final String chinaCities = "https://geoapi.qweather.com/v2/city/lookup";

    public static final String BaseUrl = "https://devapi.qweather.com/v7";

    /**
     * 实时天气
     * */
    public static final String weatherNow = "/weather/now";

    /**
     * 逐小时预报
     * */
    public static final String weather24Hour = "/weather/24h";

    /**
     * 7天天气预报
     * */
    public static final String weather7Day = "/weather/7d";

    /**
     * 实时空气质量
     * */
    public static final String airNow = "/air/now";
}
