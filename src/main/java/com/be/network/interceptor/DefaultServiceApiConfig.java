package com.be.network.interceptor;

import java.util.List;

import okhttp3.Interceptor;

/**
 * Created by Be on 2021/4/11
 */

public class DefaultServiceApiConfig implements ServiceApiConfig {

    private String mBaseUrl;
    private boolean mPrintLog;

    private boolean mEnableInspect;
    private int mTimeout;
    private List<Interceptor> mInterceptors;

    public DefaultServiceApiConfig(String baseUrl) {
        mBaseUrl = baseUrl;
    }
    public DefaultServiceApiConfig(String baseUrl,boolean printLog,boolean enableInspect) {
        mBaseUrl = baseUrl;
        mPrintLog = printLog;
        mEnableInspect = enableInspect;
    }
    @Override
    public boolean printLog() {
        return mPrintLog;
    }

    @Override
    public boolean enableInspect() {
        return mEnableInspect;
    }

    @Override
    public String getBaseUrl() {
        return mBaseUrl;
    }

    @Override
    public List<Interceptor> interceptors() {
        return mInterceptors;
    }


    @Override
    public int timeout() {
        return 15;
    }

}
