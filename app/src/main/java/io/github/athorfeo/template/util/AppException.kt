package io.github.athorfeo.template.util

import androidx.annotation.StringRes
import io.github.athorfeo.template.R
import java.lang.Exception

open class AppException(
    @StringRes val title: Int = R.string.title_default_error_dialog,
    @StringRes val description: Int = R.string.title_default_error_dialog,
    cause: Throwable? = null
): Exception(cause)
