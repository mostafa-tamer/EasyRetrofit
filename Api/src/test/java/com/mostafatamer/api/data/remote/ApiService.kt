package com.mostafatamer.api.data.remote

import com.mostafatamer.api.api_call.ApiCall
import com.mostafatamer.api.domain.model.Calendar
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {


    @GET("calendar")
    fun getCalendarWithApiCall(
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int,
    ): ApiCall<Calendar?>

    @GET("calendar")
    fun getCalendarWithCall(
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int,
    ): Call<Calendar?>

    @GET("calendar") //not correct
    fun getCalendarWithApiCallForTestingFailure(
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int,
    ): ApiCall<String?>
}