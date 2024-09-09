package io.github.athorfeo.template.util

import io.github.athorfeo.template.R
import org.junit.Assert
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class AppExceptionTest {
    @Test
    fun app_exception_test() {
        val exception = AppException()
        Assert.assertEquals(R.string.title_default_error_dialog, exception.title)
        Assert.assertEquals(R.string.title_default_error_dialog, exception.description)
        Assert.assertNull(exception.cause)

        val illegalStateException = IllegalStateException()
        val customException = AppException(
            R.string.title_not_found_item_search_dialog,
            R.string.text_not_found_item_search_dialog,
            illegalStateException
        )
        Assert.assertEquals(R.string.title_not_found_item_search_dialog, customException.title)
        Assert.assertEquals(R.string.text_not_found_item_search_dialog, customException.description)
        Assert.assertEquals(illegalStateException, customException.cause)
    }
}
