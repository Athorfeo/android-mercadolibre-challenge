package io.github.athorfeo.template.data.repository

import io.github.athorfeo.template.data.datasource.LocalItemsSearchesDataSource
import io.github.athorfeo.template.data.datasource.NetworkItemsSearchesDataSource
import io.github.athorfeo.template.data.datastore.resource.SearchedItemsResource
import io.github.athorfeo.template.model.Result
import io.github.athorfeo.template.network.response.ItemSearchItems
import io.github.athorfeo.template.network.response.SalePriceResultSearchItems
import io.github.athorfeo.template.network.response.SearchItemsResponse
import io.github.athorfeo.template.util.AppException
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.Before

class SearchItemsRepositoryTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @MockK
    private lateinit var networkItemsSearchesDataSource: NetworkItemsSearchesDataSource

    @MockK
    private lateinit var localItemsSearchesDataSource: LocalItemsSearchesDataSource

    private lateinit var repository: SearchItemsRepository

    @Before
    fun before() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        repository = SearchItemsRepository(
            networkItemsSearchesDataSource,
            localItemsSearchesDataSource
        )
    }

    @After
    fun after(){
        unmockkAll()
    }

    @Test
    fun search_items_test() {
        scope.runTest {
            val response = SearchItemsResponse(mockk(), mockk())
            coEvery { networkItemsSearchesDataSource.fetchSearchItems(any(), any(), any()) } returns response
            coEvery { localItemsSearchesDataSource.saveSearchedItems(response) } returns Unit

            repository.searchItems("", 0).collect {
                if(it is Result.Success) {
                    Assert.assertEquals(response.results, it.data)
                }
            }

            coVerify {
                networkItemsSearchesDataSource.fetchSearchItems(any(), any(), any())
                localItemsSearchesDataSource.saveSearchedItems(response)
            }
        }
    }

    @Test
    fun error_search_items_test() {
        scope.runTest {
            val exception = AppException()
            coEvery { networkItemsSearchesDataSource.fetchSearchItems(any(), any(), any()) } throws exception

            repository.searchItems("", 0).collect {
                if(it is Result.Error) {
                    Assert.assertEquals(exception, it.exception)
                }
            }

            coVerify {
                networkItemsSearchesDataSource.fetchSearchItems(any(), any(), any())
            }
        }
    }

    @Test
    fun get_last_search_test() {
        scope.runTest {
            val items = listOf<ItemSearchItems>()
            val resource = SearchedItemsResource(0, 0, items)
            coEvery { localItemsSearchesDataSource.getSearchedItems() } returns flow { emit(resource) }

            repository.getSearchedItems().collect {
                Assert.assertEquals(resource, it)
            }

            coVerify {
                localItemsSearchesDataSource.getSearchedItems()
            }
        }
    }

    @Test
    fun null_get_last_search_test() {
        scope.runTest {
            coEvery { localItemsSearchesDataSource.getSearchedItems() } returns flow { emit(null) }

            repository.getSearchedItems().collect {
                Assert.assertEquals(0, it.data.size)
            }

            coVerify {
                localItemsSearchesDataSource.getSearchedItems()
            }
        }
    }

    @Suppress("TooGenericExceptionThrown") // Only for test xD
    @Test
    fun exception_get_last_search_test() {
        scope.runTest {
            coEvery { localItemsSearchesDataSource.getSearchedItems() } returns flow {
                throw Exception()
            }

            repository.getSearchedItems().collect {
                Assert.assertEquals(0, it.data.size)
            }

            coVerify {
                localItemsSearchesDataSource.getSearchedItems()
            }
        }
    }

    @Test
    fun get_item_in_cache_test() {
        scope.runTest {
            val item = ItemSearchItems(
                "100",
                "",
                "",
                "",
                "",
                0.0,
                SalePriceResultSearchItems("", 0.0)
                , 0
            )
            val items = listOf(item)
            val resource = SearchedItemsResource(0, 0, items)
            coEvery { localItemsSearchesDataSource.getSearchedItems() } returns flow { emit(resource) }

            repository.getItem(item.id).collect {
                if(it is Result.Success) {
                    Assert.assertEquals(item, it.data)
                }
            }

            coVerify {
                localItemsSearchesDataSource.getSearchedItems()
            }
        }
    }

    @Test
    fun not_found_get_item_in_cache_test() {
        scope.runTest {
            val items = listOf<ItemSearchItems>()
            val resource = SearchedItemsResource(0, 0, items)
            coEvery { localItemsSearchesDataSource.getSearchedItems() } returns flow { emit(resource) }

            repository.getItem("").collect {
                if(it is Result.Error) {
                    Assert.assertTrue(it.exception is AppException)
                }
            }

            coVerify {
                localItemsSearchesDataSource.getSearchedItems()
            }
        }
    }

    @Test
    fun exception_get_item_in_cache_test() {
        val exception = Exception()
        scope.runTest {
            coEvery { localItemsSearchesDataSource.getSearchedItems() } returns flow {
                throw exception
            }

            repository.getItem("").collect {
                if(it is Result.Error) {
                    Assert.assertEquals(exception, it.exception)
                }
            }

            coVerify {
                localItemsSearchesDataSource.getSearchedItems()
            }
        }
    }
}
