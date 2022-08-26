package com.zero.base.baseNewNet;

import com.zero.base.baseNewNet.Interceptor.RequestInterceptor;
import com.zero.base.baseNewNet.Interceptor.ResponseInterceptor;
import com.zero.base.baseNewNet.environment.NetworkEnvironmentActivity;
import com.zero.base.baseNewNet.errorHandle.ExceptionHandle;
import com.zero.base.baseNewNet.errorHandle.HttpErrorHandle;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络Api配置
 * */
public class NetWorkApi {
    private static INetworkRequiredInfo iNetworkRequiredInfo;
    private static OkHttpClient okHttpClient;
    private static HashMap<String, Retrofit> mRetrofitMap = new HashMap<>();
    private static String mBaseUrl;
    //是否为正式环境
    private static boolean isFormal = true;

    /**
     * 初始化
     */
    public static void init(INetworkRequiredInfo networkRequiredInfo) {
        iNetworkRequiredInfo = networkRequiredInfo;
        //当初始化这个NetworkApi时，会判断当前App的网络环境
        isFormal = NetworkEnvironmentActivity.isFormalEnvironment(networkRequiredInfo.getApplicationContext());
        if (isFormal) {
            //正式环境
            mBaseUrl = "https://devapi.qweather.com/v7/";
        } else {
            //测试环境
            mBaseUrl = "https://cn.bing.com";
        }
    }

    private static String urlType(int type){
        switch (type){
            case 0://和风天气
                mBaseUrl = "https://devapi.qweather.com/v7/";
                break;
            case 1://必应每日一图
                mBaseUrl = "https://cn.bing.com/";
                break;
            case 2://搜索城市
                mBaseUrl = "https://geoapi.qweather.com/v2/";
                break;
            case 3://壁纸列表
                mBaseUrl = "http://service.picasso.adesk.com";
                break;
        }
        return mBaseUrl;
    }

    /**
     * 创建serviceClass的实例
     */
    public static <T> T createService(Class<T> serviceClass) {
        return getRetrofit(serviceClass, 0).create(serviceClass);
    }

    public static <T> T createService(Class<T> serviceClass, int type) {
        return getRetrofit(serviceClass, type).create(serviceClass);
    }

    /**
     * 配置OkHttpClient
     *
     * @return OkHttpClient
     * */
    private static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
            //设置缓存大小
            int cacheSize = 100 * 1024 *1024;
            //设置网络缓存大小
            okHttpClientBuilder.cache(new Cache(iNetworkRequiredInfo.getApplicationContext().getCacheDir(), cacheSize));
            //设置超时时长
            okHttpClientBuilder.connectTimeout(6, TimeUnit.SECONDS);
            //添加请求拦截器
            okHttpClientBuilder.addInterceptor(new RequestInterceptor(iNetworkRequiredInfo));
            //添加响应拦截器
            okHttpClientBuilder.addInterceptor(new ResponseInterceptor());
            //Debug模式下设置拦截器，打印请求日志
            if (iNetworkRequiredInfo != null && iNetworkRequiredInfo.isDebug()) {
                //初始化拦截器
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                //拦截等级设置为BODY
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                //添加拦截器
                okHttpClientBuilder.addInterceptor(loggingInterceptor);
            }
            //配置完成
            okHttpClient = okHttpClientBuilder.build();
        }
        return okHttpClient;
    }

    /**
     * 配置Retrofit
     *
     * @return Retrofit
     * */
    private static Retrofit getRetrofit(Class serviceClass, int type) {
        if (mRetrofitMap.get(urlType(type) + serviceClass.getName()) != null) {
            return mRetrofitMap.get(urlType(type) + serviceClass.getName());
        }
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        //设置base url
        retrofitBuilder.baseUrl(urlType(type));
        //设置OkHttp客户端
        retrofitBuilder.client(getOkHttpClient());
        //设置数据解析
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create());
        //设置请求回调，使用RxJava
        retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        //构建Retrofit
        Retrofit retrofit = retrofitBuilder.build();
        mRetrofitMap.put(mBaseUrl + serviceClass.getName(), retrofit);
        return retrofit;
    }

    /**
     * 错误码处理
     * */
    protected static <T> Function<T, T> getAppErrorHandle() {
        return new Function<T, T>() {
            @Override
            public T apply(T response) throws Exception {
                //response 返回500错误
                if (response instanceof BaseResponse && ((BaseResponse) response).responseCode > 500) {
                    ExceptionHandle.ServiceException serviceException = new ExceptionHandle.ServiceException();
                    serviceException.code = ((BaseResponse) response).responseCode;
                    serviceException.message = ((BaseResponse) response).responseError != null ? ((BaseResponse) response).responseError : "";
                    throw serviceException;
                }
                return response;
            }
        };
    }

    /**
     * 配置RxJava 完成线程的切换
     *
     * @param observer
     * @param <T>      泛型
     * @return Observable
     */
    public static <T>ObservableTransformer<T, T> applySchedulers(final Observer<T> observer) {
        return new ObservableTransformer<T, T>() {
            @NonNull
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                Observable observable = upstream
                        .subscribeOn(Schedulers.io())//线程订阅
                        .observeOn(AndroidSchedulers.mainThread())//观察主线程
                        .map(NetWorkApi.<T>getAppErrorHandle())//判断有没有500的错误
                        .onErrorResumeNext(new HttpErrorHandle<T>());//判断有没有400的错误
                //订阅观察者
                observable.subscribe(observer);
                return observable;
            }
        };
    }
}
