package io.github.athorfeo.template.domain

import io.github.athorfeo.template.data.datastore.resource.SearchedItemsResource
import io.github.athorfeo.template.data.repository.SearchItemsRepository
import io.github.athorfeo.template.domain.GetSearchedItemsUseCase
import io.github.athorfeo.template.network.response.ItemSearchItems
import io.github.athorfeo.template.network.response.SalePriceResultSearchItems
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.Before

class GetSearchedItemsUseCaseTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @MockK
    private lateinit var searchItemsRepository: SearchItemsRepository

    private lateinit var usecase: GetSearchedItemsUseCase

    @Before
    fun before() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        usecase = GetSearchedItemsUseCase(searchItemsRepository)
    }

    @After
    fun after(){
        unmockkAll()
    }

    @Test
    fun get_last_search_test() {
        scope.runTest {
            val item = ItemSearchItems(
                "100",
                "",
            "",
            "",
            "",
                0.0,
                SalePriceResultSearchItems("", 0.0),
                0
            )
            val list = listOf(item)
            val resource = SearchedItemsResource(0, 0, list)
            coEvery { searchItemsRepository.getSearchedItems() } returns flow { emit(resource) }

            usecase.getPaging().collect {
                Assert.assertEquals(list[0].id, it.items[0].id)
            }

            coVerify { searchItemsRepository.getSearchedItems() }
        }
    }
}
