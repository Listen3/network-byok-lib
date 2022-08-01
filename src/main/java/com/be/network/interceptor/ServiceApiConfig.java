package com.be.network.interceptor;

import java.util.List;

import okhttp3.Interceptor;

/**
 * Created by Be on 2021/4/11
 */
public interface ServiceApiConfig {
    /**
     * 是否打印日志
     *
     * @return
     */
    boolean printLog();

    /**
     * 是否启用
     *
     * @return
     */
    boolean enableInspect();


    String getBaseUrl();

    /**
     * 拦截器
     *
     * @return
     */
    List<Interceptor> interceptors();

    /**
     * 超时时间
     */
    int timeout();
}
