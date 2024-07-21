package com.mostafatamer.api.data.remote;

import retrofit2.Call;
import retrofit2.http.GET;

interface Service {

    @GET("data")
    Call<String> getDataFromTheServer();
}
