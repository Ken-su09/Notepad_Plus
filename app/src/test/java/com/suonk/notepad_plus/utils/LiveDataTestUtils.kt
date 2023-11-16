package com.suonk.notepad_plus.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.rule.ServiceTestRule.withTimeout
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.time.withTimeout

fun <T> LiveData<T>.observeForTesting(
    testScope: TestScope,
    pause: Boolean = false,
    block: (LiveData<T>) -> Unit
) {
    val observer = Observer<T> { }
    try {
        observeForever(observer)
        if (!pause) {
            testScope.runCurrent()
        }
        block(this)
    } finally {
        removeObserver(observer)
    }
}

//suspend fun <T> LiveData<T>.observeForTestingWithTimeout(
//    timeoutMillis: Long,
//    block: suspend (LiveData<T>) -> Unit
//) {
//    val observer = Observer<T> {}
//    withTimeout(timeoutMillis) {
//        try {
//            observeForever(observer)
//            block(this@observeForTestingWithTimeout)
//        } finally {
//            removeObserver(observer)
//        }
//    }
//}