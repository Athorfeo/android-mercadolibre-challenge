package io.github.athorfeo.template.data.datasource

import io.github.athorfeo.template.network.NetworkExecutor
import io.github.athorfeo.template.network.api.ItemsSearchesApi
import io.github.athorfeo.template.network.response.SearchItemsResponse
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
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

class NetworkItemsSearchesDataSourceTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @MockK
    private lateinit var networkExecutor: NetworkExecutor

    @MockK
    private lateinit var api: ItemsSearchesApi

    private lateinit var dataSource: NetworkItemsSearchesDataSource

    @Before
    fun before() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        dataSource = NetworkItemsSearchesDataSource(
            networkExecutor,
            api
        )
    }

    @After
    fun after(){
        unmockkAll()
    }

    @Test
    fun fetch_search_items_test() {
        scope.runTest {
            val response: SearchItemsResponse = mockk()
            coEvery { api.fetchSearchItems(any(), any(), any()) } returns Response.success(response)
            coEvery { networkExecutor.fetch(any<(suspend () -> Response<SearchItemsResponse>)>()) } coAnswers {
                (arg(0) as (suspend () -> Response<SearchItemsResponse>)).invoke()
                response
            }

            val fetchResponse = dataSource.fetchSearchItems("", 0, 0)
            Assert.assertEquals(response, fetchResponse)

            coVerify {
                api.fetchSearchItems(any(), any(), any())
                networkExecutor.fetch(any<(suspend () -> Response<SearchItemsResponse>)>())
            }
        }
    }

    @Test(expected = Exception::class)
    fun null_fetch_search_items_test() {
        scope.runTest {
            val response: SearchItemsResponse = mockk()
            coEvery { api.fetchSearchItems(any(), any(), any()) } returns Response.success(response)
            coEvery { networkExecutor.fetch(any<(suspend () -> Response<SearchItemsResponse>)>()) } coAnswers {
                (arg(0) as (suspend () -> Response<SearchItemsResponse>)).invoke()
                null
            }

            dataSource.fetchSearchItems("", 0, 0)
        }
    }
}
