package io.github.athorfeo.template.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.junit.Assert
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TypeTest {
    @Test
    fun type_test() {
        Assert.assertEquals(FontFamily.Default, Typography.bodyLarge.fontFamily)
        Assert.assertEquals(FontWeight.Normal, Typography.bodyLarge.fontWeight)
        Assert.assertEquals(16.sp, Typography.bodyLarge.fontSize)
        Assert.assertEquals(24.sp, Typography.bodyLarge.lineHeight)
        Assert.assertEquals(0.5.sp, Typography.bodyLarge.letterSpacing)
    }
}
