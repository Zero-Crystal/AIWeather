package com.zero.base.baseNewNet.errorHandle;


import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * 网络错误处理
 * */
public class HttpErrorHandle<T> implements Function<Throwable, Observable<T>> {

    /**
     * 处理以下两类网络错误：
     * 1、http请求相关的错误，例如：404，403，socket timeout等等；
     * 2、应用数据的错误会抛RuntimeException，最后也会走到这个函数来统一处理；
     */
    @Override
    public Observable<T> apply(@NonNull Throwable throwable) throws Exception {
        return Observable.error(ExceptionHandle.handlerException(throwable));
    }
}
