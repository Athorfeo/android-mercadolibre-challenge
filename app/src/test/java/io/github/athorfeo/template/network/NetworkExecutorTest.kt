package io.github.athorfeo.template.network

import io.github.athorfeo.template.util.AppException
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.Before
import retrofit2.Response

class NetworkExecutorTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @MockK
    private lateinit var networkConnectionHelper: NetworkConnectionHelper

    private lateinit var networkExecutor: NetworkExecutor

    @Before
    fun before() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        networkExecutor = NetworkExecutor(dispatcher, networkConnectionHelper)
    }

    @After
    fun after(){
        unmockkAll()
    }

    @Test(expected = AppException::class)
    fun error_internet_connection_success_fetch_test() {
        scope.runTest {
            val response = Response.success(Unit)
            every { networkConnectionHelper.hasNetworkConnection } returns false
            networkExecutor.fetch { response }
        }
    }

    @Test(expected = AppException::class)
    fun not_success_fetch_test() {
        scope.runTest {
            every { networkConnectionHelper.hasNetworkConnection } returns true
            val response: Response<Unit> = mockk()
            every { response.isSuccessful } returns false
            networkExecutor.fetch { response }
        }
    }

    @Test
    fun success_fetch_test() {
        scope.runTest {
            val model = Unit
            val response = Response.success(model)
            every { networkConnectionHelper.hasNetworkConnection } returns true
            Assert.assertEquals(model, networkExecutor.fetch { response })
        }
    }

    @Test
    fun not_body_success_fetch_test() {
        scope.runTest {
            val response = Response.success<Unit>(null)
            every { networkConnectionHelper.hasNetworkConnection } returns true
            Assert.assertNull(networkExecutor.fetch { response })
        }
    }
}
