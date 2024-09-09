package io.github.athorfeo.template.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.google.gson.Gson
import io.github.athorfeo.template.data.datastore.SEARCH_ITEMS_STORE
import io.github.athorfeo.template.data.datastore.resource.SearchedItemsResource
import io.github.athorfeo.template.network.response.ItemSearchItems
import io.github.athorfeo.template.network.response.PagingSearchItems
import io.github.athorfeo.template.network.response.SearchItemsResponse
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.Before

class LocalItemsSearchesDataSourceTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @MockK
    private lateinit var cacheDataStore: DataStore<Preferences>

    @MockK
    private lateinit var serializer: Gson

    private lateinit var dataSource: LocalItemsSearchesDataSource

    @Before
    fun before() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        dataSource = LocalItemsSearchesDataSource(
            cacheDataStore,
            serializer
        )
    }

    @After
    fun after(){
        unmockkAll()
    }

    @Test
    fun save_searched_items_test() {
        scope.runTest {
            val preferences: Preferences = mockk()
            val mutablePreferences: MutablePreferences = mockk()

            mockkStatic(DataStore<Preferences>::edit)
            coEvery { cacheDataStore.edit(any()) } coAnswers {
                (arg(1) as (suspend (MutablePreferences) -> Unit)).invoke(mutablePreferences)
                preferences
            }
            every { serializer.toJson(any<SearchedItemsResource>()) } returns ""
            every { mutablePreferences[SEARCH_ITEMS_STORE] = any() } returns Unit

            val pagingSearchItems = PagingSearchItems(0,0,0,0)
            val items = listOf<ItemSearchItems>()
            val response = SearchItemsResponse(pagingSearchItems, items)
            dataSource.saveSearchedItems(response)

            coVerify {
                cacheDataStore.edit(any())
            }

            verify {
                serializer.toJson(any<List<ItemSearchItems>>())
                mutablePreferences[SEARCH_ITEMS_STORE] = any()
            }
        }
    }

    @Test
    fun get_searched_items_test() {
        scope.runTest {
            val preferences: Preferences = mockk()
            coEvery { cacheDataStore.data } returns flow { emit(preferences) }
            every { preferences[SEARCH_ITEMS_STORE] } returns ""

            val list = listOf<ItemSearchItems>()
            val resource = SearchedItemsResource(0, 0, list)
            every { serializer.fromJson(any<String>(), SearchedItemsResource::class.java) } returns resource

            dataSource.getSearchedItems().collect {
                Assert.assertEquals(resource, it)
            }

            coVerify {
                cacheDataStore.data
            }

            verify {
                preferences[SEARCH_ITEMS_STORE]
                serializer.fromJson(any<String>(), SearchedItemsResource::class.java)
            }
        }
    }

    @Test
    fun null_get_searched_items_test() {
        scope.runTest {
            val preferences: Preferences = mockk()
            coEvery { cacheDataStore.data } returns flow { emit(preferences) }
            every { preferences[SEARCH_ITEMS_STORE] } returns null

            dataSource.getSearchedItems().collect {
                Assert.assertEquals(null, it)
            }

            coVerify {
                cacheDataStore.data
            }

            verify {
                preferences[SEARCH_ITEMS_STORE]
            }
        }
    }
}
