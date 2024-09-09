package io.github.athorfeo.template.network.api

import io.mockk.MockKAnnotations
import io.mockk.unmockkAll
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.Before

class BuildApiHelperTest {
    @Before
    fun before() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @After
    fun after(){
        unmockkAll()
    }

    @Test
    fun debug_build_http_client_test() {
        val httpClient = buildHttpClient(true)
        Assert.assertEquals(1, httpClient.interceptors.size)
        Assert.assertTrue(httpClient.interceptors[0] is HttpLoggingInterceptor)
    }

    @Test
    fun release_build_http_client_test() {
        val httpClient = buildHttpClient(false)
        Assert.assertEquals(0, httpClient.interceptors.size)
    }
}
