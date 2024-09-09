package io.github.athorfeo.template.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkRequest
import io.github.athorfeo.template.util.Logger
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Test
import org.junit.Before

class NetworkConnectionHelperTest {
    @MockK
    private lateinit var logger: Logger

    @MockK
    private lateinit var context: Context

    private lateinit var helper: NetworkConnectionHelper

    @Before
    fun before() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        helper = NetworkConnectionHelper(logger)
    }

    @After
    fun after(){
        unmockkAll()
    }

    @Test
    fun register_network_callback_test() {
        mockkConstructor(NetworkRequest.Builder::class)
        val networkRequestBuilder: NetworkRequest.Builder = mockk()
        val networkRequest: NetworkRequest = mockk()
        every { anyConstructed<NetworkRequest.Builder>().addTransportType(any()) } returns networkRequestBuilder
        every { networkRequestBuilder.addTransportType(any()) } returns networkRequestBuilder
        every { networkRequestBuilder.build() } returns networkRequest

        val connectivityManager: ConnectivityManager = mockk()
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        every {
            connectivityManager.registerNetworkCallback(
                any<NetworkRequest>(),
                any<ConnectivityManager.NetworkCallback>()
            )
        } returns Unit
        every { logger.d(any()) } returns Unit

        helper.registerNetworkCallback(context)

        verify {
            anyConstructed<NetworkRequest.Builder>().addTransportType(any())
            networkRequestBuilder.addTransportType(any())
            networkRequestBuilder.build()
            context.getSystemService(Context.CONNECTIVITY_SERVICE)
            connectivityManager.registerNetworkCallback(
                any<NetworkRequest>(),
                any<ConnectivityManager.NetworkCallback>()
            )
            logger.d(any())
        }
    }

    @Test
    fun null_register_network_callback_test() {
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns null
        every { logger.e(any<String>()) } returns Unit

        helper.registerNetworkCallback(context)

        verify {
            context.getSystemService(Context.CONNECTIVITY_SERVICE)
            logger.e(any<String>())
        }
    }
}
