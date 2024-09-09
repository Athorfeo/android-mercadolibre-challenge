package io.github.athorfeo.template.ui.search.query

import io.github.athorfeo.template.data.repository.SearchItemsRepository
import io.github.athorfeo.template.model.Result
import io.github.athorfeo.template.network.response.ItemSearchItems
import io.github.athorfeo.template.util.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.Before
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
class QuerySearchViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var searchItemsRepository: SearchItemsRepository

    private lateinit var viewModel: QuerySearchViewModel

    @Before
    fun before() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        viewModel = QuerySearchViewModel(searchItemsRepository)
    }

    @After
    fun after() {
        unmockkAll()
    }

    @Test
    fun init_query_search_state_test() = runTest {
        val jobUiLogicState = launch(UnconfinedTestDispatcher()){ viewModel.uiLogicState.collect() }
        val jobQuery = launch(UnconfinedTestDispatcher()){ viewModel.query.collect() }

        Assert.assertFalse(viewModel.uiLogicState.value.isLoading)
        Assert.assertNull(viewModel.uiLogicState.value.exception)
        Assert.assertEquals("", viewModel.query.value)

        jobUiLogicState.cancel()
        jobQuery.cancel()
    }

    @Test
    fun on_dismiss_error_test() = runTest {
        val jobUiLogicState = launch(UnconfinedTestDispatcher()){ viewModel.uiLogicState.collect() }
        val jobQuery = launch(UnconfinedTestDispatcher()){ viewModel.query.collect() }

        val exception = Exception()
        every { searchItemsRepository.searchItems(any(), any()) } returns flow { emit(Result.Error(exception)) }

        viewModel.onSearch(mockk())

        Assert.assertFalse(viewModel.uiLogicState.value.isLoading)
        Assert.assertEquals(exception, viewModel.uiLogicState.value.exception?.cause)
        Assert.assertEquals("", viewModel.query.value)

        viewModel.onDismissError()

        Assert.assertFalse(viewModel.uiLogicState.value.isLoading)
        Assert.assertNull(viewModel.uiLogicState.value.exception)
        Assert.assertEquals("", viewModel.query.value)

        verify {
            searchItemsRepository.searchItems(any(), any())
        }

        jobUiLogicState.cancel()
        jobQuery.cancel()
    }

    @Test
    fun on_query_change_test() = runTest {
        val jobUiLogicState = launch(UnconfinedTestDispatcher()){ viewModel.uiLogicState.collect() }
        val jobQuery = launch(UnconfinedTestDispatcher()){ viewModel.query.collect() }

        val query = "query"
        viewModel.onQueryChange(query)

        Assert.assertFalse(viewModel.uiLogicState.value.isLoading)
        Assert.assertNull(viewModel.uiLogicState.value.exception)
        Assert.assertEquals(query, viewModel.query.value)

        jobUiLogicState.cancel()
        jobQuery.cancel()
    }

    @Test
    fun loading_on_search_test() = runTest {
        val jobUiLogicState = launch(UnconfinedTestDispatcher()){ viewModel.uiLogicState.collect() }
        val jobQuery = launch(UnconfinedTestDispatcher()){ viewModel.query.collect() }

        every { searchItemsRepository.searchItems(any(), any()) } returns flow { emit(Result.Loading) }

        viewModel.onSearch(mockk())

        Assert.assertTrue(viewModel.uiLogicState.value.isLoading)
        Assert.assertNull(viewModel.uiLogicState.value.exception)
        Assert.assertEquals("", viewModel.query.value)

        verify {
            searchItemsRepository.searchItems(any(), any())
        }

        jobUiLogicState.cancel()
        jobQuery.cancel()
    }

    @Test
    fun error_on_search_test() = runTest {
        val jobUiLogicState = launch(UnconfinedTestDispatcher()){ viewModel.uiLogicState.collect() }
        val jobQuery = launch(UnconfinedTestDispatcher()){ viewModel.query.collect() }

        val exception = Exception()
        every { searchItemsRepository.searchItems(any(), any()) } returns flow { emit(Result.Error(exception)) }

        viewModel.onSearch(mockk())

        Assert.assertFalse(viewModel.uiLogicState.value.isLoading)
        Assert.assertEquals(exception, viewModel.uiLogicState.value.exception?.cause)
        Assert.assertEquals("", viewModel.query.value)

        verify {
            searchItemsRepository.searchItems(any(), any())
        }

        jobUiLogicState.cancel()
        jobQuery.cancel()
    }

    @Test
    fun success_on_search_test() = runTest {
        val jobUiLogicState = launch(UnconfinedTestDispatcher()){ viewModel.uiLogicState.collect() }
        val jobQuery = launch(UnconfinedTestDispatcher()){ viewModel.query.collect() }

        every { searchItemsRepository.searchItems(any(), any()) } returns flow { emit(Result.Success(1)) }

        val onSuccess: (String) -> Unit = mockk()
        every { onSuccess.invoke(any()) } returns Unit

        viewModel.onSearch(onSuccess)

        verify {
            searchItemsRepository.searchItems(any(), any())
            onSuccess.invoke(any())
        }

        jobUiLogicState.cancel()
        jobQuery.cancel()
    }

    @Test
    fun empty_list_success_on_search_test() = runTest {
        val jobUiLogicState = launch(UnconfinedTestDispatcher()){ viewModel.uiLogicState.collect() }
        val jobQuery = launch(UnconfinedTestDispatcher()){ viewModel.query.collect() }

        every { searchItemsRepository.searchItems(any(), any()) } returns flow { emit(Result.Success(0)) }

        viewModel.onSearch(mockk())

        Assert.assertFalse(viewModel.uiLogicState.value.isLoading)
        Assert.assertNotNull(viewModel.uiLogicState.value.exception)
        Assert.assertEquals("", viewModel.query.value)

        verify {
            searchItemsRepository.searchItems(any(), any())
        }

        jobUiLogicState.cancel()
        jobQuery.cancel()
    }
}
