package com.mostafatamer.apiCall

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun addLatchTimeout(latch: CountDownLatch) {
    if (!latch.await(10, TimeUnit.SECONDS)) {
        throw AssertionError("Callback was not called within the timeout")
    }
}