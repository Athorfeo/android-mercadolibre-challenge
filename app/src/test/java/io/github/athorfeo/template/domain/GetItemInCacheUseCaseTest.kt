package io.github.athorfeo.template.domain

import io.github.athorfeo.template.data.repository.SearchItemsRepository
import io.github.athorfeo.template.domain.GetItemInCacheUseCase
import io.github.athorfeo.template.model.Result
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

class GetItemInCacheUseCaseTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @MockK
    private lateinit var searchItemsRepository: SearchItemsRepository

    private lateinit var usecase: GetItemInCacheUseCase

    @Before
    fun before() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        usecase = GetItemInCacheUseCase(searchItemsRepository)
    }

    @After
    fun after(){
        unmockkAll()
    }

    @Test
    fun loading_get_by_id_test() {
        scope.runTest {
            coEvery { searchItemsRepository.getItemInCache(any()) } returns flow {
                emit(Result.Loading)
            }

            usecase.getById("").collect {
                Assert.assertTrue(it.isLoading)
                Assert.assertNull(it.exception)
                Assert.assertNull(it.item)
            }

            coVerify { searchItemsRepository.getItemInCache(any()) }
        }
    }

    @Test
    fun error_get_by_id_test() {
        scope.runTest {
            val exception = Exception()
            coEvery { searchItemsRepository.getItemInCache(any()) } returns flow {
                emit(Result.Error(exception))
            }

            usecase.getById("").collect {
                Assert.assertFalse(it.isLoading)
                Assert.assertEquals(exception, it.exception?.cause)
                Assert.assertNull(it.item)
            }

            coVerify { searchItemsRepository.getItemInCache(any()) }
        }
    }

    @Test
    fun success_get_by_id_test() {
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
            coEvery { searchItemsRepository.getItemInCache(any()) } returns flow {
                emit(Result.Success(item))
            }

            usecase.getById("").collect {
                Assert.assertFalse(it.isLoading)
                Assert.assertNull(it.exception)
                Assert.assertNotNull(it.item)
            }

            coVerify { searchItemsRepository.getItemInCache(any()) }
        }
    }
}
