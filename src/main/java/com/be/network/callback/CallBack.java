package com.be.network.callback;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Response;


/**
 * Created by Be on 2021/4/13.
 */

public abstract class CallBack<T> implements retrofit2.Callback<T> {

    @Override
    public void onResponse(final Call<T> call, Response<T> response) {
        try {
            T data = response.body();
            if (data != null) {
                success(data);
            } else {
                failure(new Exception(String.format("解析异常：%s", new Gson().toJson(response))));
            }

        } catch (Exception e) {
            failure(e);
            e.printStackTrace();
        }

    }


    public abstract void success(T data);

    public abstract void failure(Throwable e);

    @Override
    public void onFailure(Call call, Throwable t) {
        failure(t);
        t.printStackTrace();
    }

}