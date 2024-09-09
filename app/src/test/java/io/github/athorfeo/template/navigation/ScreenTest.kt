package io.github.athorfeo.template.navigation

import org.junit.Assert
import org.junit.Test

class ScreenTest {
    @Test
    fun query_screen_test() {
        Assert.assertEquals(Screen.QuerySearch.route, Screen.QuerySearch.buildRoute())
    }

    @Test
    fun result_search_screen_test() {
        val route = "${Screen.ResultSearch.route}/{${Screen.QUERY_ARG}}"
        Assert.assertEquals(route, Screen.ResultSearch.buildRoute())

        val arg = "arg"
        val navigate = "${Screen.ResultSearch.route}/arg"
        Assert.assertEquals(navigate, Screen.ResultSearch.buildNavigate(arg))
    }

    @Test
    fun detail_item_screen_test() {
        val route = "${Screen.DetailItem.route}/{${Screen.ITEM_ID_ARG}}"
        Assert.assertEquals(route, Screen.DetailItem.buildRoute())

        val arg = "arg"
        val navigate = "${Screen.DetailItem.route}/arg"
        Assert.assertEquals(navigate, Screen.DetailItem.buildNavigate(arg))
    }

    @Test
    fun root_screens_test() {
        Assert.assertEquals(1, rootScreens.size)
        Assert.assertTrue(rootScreens.contains(Screen.QuerySearch))
    }
}
