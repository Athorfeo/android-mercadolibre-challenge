package io.github.athorfeo.template.util

import android.util.Log
import java.lang.Exception

object AppLogger: Logger {
    override fun d(text: String) {
        Log.d(Logger.APPLICATION_TAG, "${Logger.DEBUG_TAG}: $text")
    }

    override fun e(text: String) {
        Log.e(Logger.APPLICATION_TAG, "${Logger.ERROR_TAG}: $text")
    }

    override fun e(exception: Exception) {
        Log.e(Logger.APPLICATION_TAG, Logger.ERROR_TAG, exception)
    }
}
