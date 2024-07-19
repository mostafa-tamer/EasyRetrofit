package com.mostafatamer.apiservice;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Request;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ApiCallImpl<T> extends ApiCall<T> {
    private final Call<T> call;

    ApiCallImpl(Call<T> call) {
        this.call = call;
    }

    @NonNull
    @Override
    public Response<T> execute() throws IOException {
        return call.execute();
    }

    @Override
    public void enqueue(@NonNull Callback<T> callback) {
        call.enqueue(callback);
    }

    @Override
    public boolean isExecuted() {
        return call.isExecuted();
    }

    @Override
    public void cancel() {
        call.cancel();
    }

    @Override
    public boolean isCanceled() {
        return call.isCanceled();
    }

    @NonNull
    @Override
    public Request request() {
        return call.request();
    }

    @NonNull
    @Override
    public Timeout timeout() {
        return call.timeout();
    }
}
