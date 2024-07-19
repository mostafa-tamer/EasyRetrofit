package com.mostafatamer.apiservice;

import androidx.annotation.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

public class ApiCallAdapterFactory extends CallAdapter.Factory {

    @Override
    public CallAdapter<?, ?> get(@NonNull Type returnType, @NonNull Annotation[] annotations, @NonNull Retrofit retrofit) {
        if (getRawType(returnType) != ApiCall.class) {
            return null;
        }

        final Type responseType = getParameterUpperBound(0, (ParameterizedType) returnType);

        return new CallAdapter<Object, ApiCall<?>>() {
            @NonNull
            @Override
            public Type responseType() {
                return responseType;
            }

            @NonNull
            @Override
            public ApiCall<?> adapt(@NonNull Call<Object> call) {
                return new ApiCallImpl<>(call);
            }
        };
    }
}

