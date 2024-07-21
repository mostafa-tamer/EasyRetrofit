package com.mostafatamer.api.api_call;

import androidx.annotation.NonNull;

import com.mostafatamer.api.Api;

import retrofit2.Call;

public abstract class ApiCall<T> extends Api<T> implements Call<T> {

    /**
     * Begins the API request.
     */
    @Override
    public void beginRequest() {
        super.beginRequest(this);
    }

    /** @noinspection unchecked*/
    @NonNull
    @Override
    public Call<T> clone() {
        try {
            return (Call<T>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
