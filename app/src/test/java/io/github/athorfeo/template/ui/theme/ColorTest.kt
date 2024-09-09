package io.github.athorfeo.template.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ColorTest {
    @Test
    fun color_test() {
        val purple80 = Color(0xFFD0BCFF)
        val purpleGrey80 = Color(0xFFCCC2DC)
        val pink80 = Color(0xFFEFB8C8)

        val purple40 = Color(0xFF6650a4)
        val purpleGrey40 = Color(0xFF625b71)
        val pink40 = Color(0xFF7D5260)

        Assert.assertEquals(purple80, Purple80)
        Assert.assertEquals(purpleGrey80, PurpleGrey80)
        Assert.assertEquals(pink80, Pink80)
        Assert.assertEquals(purple40, Purple40)
        Assert.assertEquals(purpleGrey40, PurpleGrey40)
        Assert.assertEquals(pink40, Pink40)
    }
}
