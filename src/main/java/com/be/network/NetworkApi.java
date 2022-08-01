package com.be.network;

import com.be.network.interceptor.ServiceApiConfig;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Be on 2021/4/11
 */
public class NetworkApi {

    private static HashMap<String, Retrofit> mMapRetrofit = new HashMap<>();
    private static boolean stethoInitialized;

    public static <T> void registerServiceApi(Class<T> cls, ServiceApiConfig serviceApiConfig) {

        OkHttpClient client = getOkHttpClient(serviceApiConfig);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(serviceApiConfig.getBaseUrl())
                .build();

        String serviceApiKey = cls.getName();
        mMapRetrofit.put(serviceApiKey, retrofit);
    }


    private static OkHttpClient getOkHttpClient(ServiceApiConfig config) {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (config.printLog()) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }
        if (config.enableInspect()) {
            builder.addNetworkInterceptor(new StethoInterceptor());
        }

        List<Interceptor> interceptors = config.interceptors();

        if (interceptors != null && !interceptors.isEmpty()) {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }

        if (config.timeout() > 0) {
            builder.connectTimeout(config.timeout(), TimeUnit.SECONDS);
        }

        return builder.build();
    }


    private static <T> Retrofit getRetrofit(Class<T> cls) {

        String clsName = cls.getName();
        if (!mMapRetrofit.containsKey(clsName)) {
            throw new RuntimeException(String.format("Api interface %s is unregistered", cls));
        }

        return mMapRetrofit.get(clsName);
    }


    public static <T> ObservableTransformer<T, T> applySchedulers(Observer<T> observer) {

        return new ObservableTransformer<T, T>() {
            @NonNull
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                Observable<T> observable = upstream
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                observable.subscribe(observer);
                return observable;
            }
        };

    }

    public static <T> T getServiceApi(Class<T> cls) {
        return getRetrofit(cls).create(cls);
    }

    public static void clear() {
        mMapRetrofit.clear();
    }
}
