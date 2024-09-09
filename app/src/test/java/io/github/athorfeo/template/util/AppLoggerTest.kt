package io.github.athorfeo.template.util

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class AppLoggerTest {
    @Test
    fun logger_test() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        AppLogger.d("Text")
        verify { Log.d(any(), any()) }

        AppLogger.e("Text")
        verify { Log.e(any(), any()) }

        AppLogger.e(AppException())
        verify { Log.e(any(), any(), any()) }
    }
}
