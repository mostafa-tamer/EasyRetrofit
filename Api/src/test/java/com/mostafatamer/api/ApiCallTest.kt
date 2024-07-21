package com.mostafatamer.api

import com.mostafatamer.api.api_decorator.ApiDecorator
import com.mostafatamer.api.data.remote.ApiService
import com.mostafatamer.api.data.remote.RetrofitClient
import com.mostafatamer.api.domain.model.Calendar
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.CountDownLatch


class ApiCallTest {

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

    @Test
    fun test_retrofit_response_using_api_call() {
        apiService.getCalendarWithApiCall(2024, 12, 30.0, 30.0, 5)
            .setOnResponse { data, code ->
                assert(data != null)
                assertEquals(code, 200)
                latchWithOneCount.countDown()
            }.beginRequest()

        addLatchTimeout(latchWithOneCount)
    }

    @Test
    fun test_retrofit_startEvent_responseEvent_endEvent_using_api_call() {
        val latch = CountDownLatch(3)

        var started = false
        var ended = false

        apiService.getCalendarWithApiCall(2024, 12, 30.0, 30.0, 5)
            .setOnStart {
                started = true
                latch.countDown()
            }.setOnResponse { _, code ->
                assertEquals(code, 200)
                latch.countDown()
            }.setOnEnd {
                ended = true
                latch.countDown()
            }.beginRequest()

        addLatchTimeout(latch)

        latch.await()

        assert(started)
        assert(ended)
    }

    @Test
    fun test_retrofit_startEvent_responseEvent_endEvent_blocking_busyEvent_loadingStateObserver() {
        val latch = CountDownLatch(5)

        var busy = 0
        var started = false
        var ended = false

        var loadingObserverCounter = 0

        apiService.getCalendarWithApiCall(2024, 12, 30.0, 30.0, 5)
            .allowBlocking {
                busy++
                latch.countDown()
            }.setLoadingStateObserver {
                when (loadingObserverCounter) {
                    0 -> assertTrue(it)
                    1 -> assertFalse(it)
                    else -> assert(false)
                }
                loadingObserverCounter++
            }.setOnStart {
                started = true
                latch.countDown()
            }.setOnResponse { _, code ->
                assertEquals(code, 200)
                latch.countDown()
            }.setOnEnd {
                ended = true
                latch.countDown()
            }.beginRequest()

        dummyApiCall()
        dummyApiCall()

        addLatchTimeout(latch)

        latch.await()

        assertEquals(loadingObserverCounter, 2)
        assert(started)
        assert(ended)
        assertEquals(busy,2)
    }

    @Test
    fun test_retrofit_response_globalLoadingStateObserver() {

        var loadingObserverCounter = 0

        Api.setGlobalLoadingStateObserver {
            when (loadingObserverCounter) {
                0 -> assertTrue(it)
                1 -> assertFalse(it)
                else -> assert(false)
            }
            loadingObserverCounter++
        }

        ApiDecorator(apiService.getCalendarWithApiCall(2024, 12, 30.0, 30.0, 5))
            .setOnResponse { _, _ ->
                latchWithOneCount.countDown()
            }.beginRequest()

        addLatchTimeout(latchWithOneCount)

        latchWithOneCount.await()



        assertEquals(loadingObserverCounter, 2)

        Api.setGlobalLoadingStateObserver(null)    //clean
    }


    private fun dummyApiCall() {
        ApiDecorator(apiService.getCalendarWithApiCall(2024, 12, 30.0, 30.0, 5))
            .beginRequest()
    }


    @Test
    fun test_failure() {
        ApiDecorator(apiService.getCalendarWithApiCallForTestingFailure(2024, 12, 30.0, 30.0, 5))
            .setOnFailure {
                assert(it != null)
                latchWithOneCount.countDown()
            }.beginRequest()

        addLatchTimeout(latchWithOneCount)
    }

    @Test
    fun test_number_of_running_service() {
        dummyApiCall()
        assertEquals(Api.getNumberOfRunningServices(), 1)
        dummyApiCall()
        assertEquals(Api.getNumberOfRunningServices(), 2)
        dummyApiCall()
        assertEquals(Api.getNumberOfRunningServices(), 3)
        dummyApiCall()
        assertEquals(Api.getNumberOfRunningServices(), 4)
    }

    @Test
    fun test_normal_callback() {
        apiService.getCalendarWithApiCall(2024, 12, 30.0, 30.0, 5)
            .enqueue(object : Callback<Calendar?> {
                override fun onResponse(
                    call: Call<Calendar?>,
                    response: Response<Calendar?>,
                ) {
                    assert(response.body() != null)
                    assertEquals(response.code(), 200)
                    latchWithOneCount.countDown()
                }

                override fun onFailure(call: Call<Calendar?>, t: Throwable) {
                    assert(t.cause == null)
                }
            })

        addLatchTimeout(latchWithOneCount)
    }
}