package com.zero.base.baseNet;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求服务构建类
 * */
public class ServiceGenerator {
    public static final String BaseUrl = "https://devapi.qweather.com/v7";

    public static <T> T createService(Class<T> service) {
        //创建OkHttpClient构建器对象
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        //设置6秒超时时间
        okHttpClientBuilder.connectTimeout(6 * 1000, TimeUnit.MILLISECONDS);
        //设置消息拦截器，打印请求日志
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClientBuilder.addInterceptor(loggingInterceptor);
        //在Retrofit中设置httpclient
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClientBuilder.build())
                .build();
        return retrofit.create(service);
    }
}
