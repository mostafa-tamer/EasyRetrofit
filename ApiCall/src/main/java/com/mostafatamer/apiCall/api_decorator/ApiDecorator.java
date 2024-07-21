package com.mostafatamer.apiCall.api_decorator;

import com.mostafatamer.apiCall.Api;

import retrofit2.Call;

public class ApiDecorator<T> extends Api<T> {

    private final Call<T> call;

    public ApiDecorator(Call<T> call) {
        this.call = call;
    }

    /**
     * Begins the API request.
     */
    @Override
    public void beginRequest() {
        beginRequest(call);
    }
}
