package com.mostafatamer.api

import com.mostafatamer.api.api_decorator.ApiDecorator
import com.mostafatamer.api.data.remote.ApiService
import com.mostafatamer.api.data.remote.RetrofitClient
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
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