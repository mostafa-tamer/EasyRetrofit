package com.mostafatamer.apiCall

import com.mostafatamer.apiCall.api_decorator.ApiDecorator
import com.mostafatamer.apiCall.data.remote.ApiService
import com.mostafatamer.apiCall.data.remote.RetrofitClient
import com.mostafatamer.apiCall.domain.model.Calendar
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.CountDownLatch


class ApiDecoratorTest {

    private lateinit var apiService: ApiService
    private val latchWithOneCount = CountDownLatch(1)

    @Before
    fun setUp() {
        val retrofit = RetrofitClient.getInstance().retrofit
        apiService = retrofit.create(ApiService::class.java)
    }

    @Test
    fun test_retrofit_response_test_using_api_decorator() {
        ApiDecorator(apiService.getCalendarWithApiCall(2024, 12, 30.0, 30.0, 5))
            .setOnResponse { _, code ->
                assertEquals(code, 200)
                latchWithOneCount.countDown()
            }.beginRequest()

        addLatchTimeout(latchWithOneCount)
    }
}