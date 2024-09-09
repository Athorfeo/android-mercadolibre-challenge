package io.github.athorfeo.template.ui.search.result

import androidx.lifecycle.SavedStateHandle
import io.github.athorfeo.template.data.repository.SearchItemsRepository
import io.github.athorfeo.template.domain.CalculateOffsetPagingUseCase
import io.github.athorfeo.template.domain.GetSearchedItemsUseCase
import io.github.athorfeo.template.model.Item
import io.github.athorfeo.template.model.Result
import io.github.athorfeo.template.model.SearchedItemsPaging
import io.github.athorfeo.template.navigation.Screen
import io.github.athorfeo.template.util.MainDispatcherRule
import io.mockk.MockKAnnotations
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
class ResultSearchViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var savedStateHandle: SavedStateHandle

    @MockK
    private lateinit var fetchSearchItems: GetSearchedItemsUseCase

    @MockK
    private lateinit var searchItemsRepository: SearchItemsRepository

    @MockK
    private lateinit var calculateOffsetPaging: CalculateOffsetPagingUseCase

    @Before
    fun before() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @After
    fun after() {
        unmockkAll()
    }

    @Test
    fun init_ui_state_test()= runTest {
        val query = "query"
        every { savedStateHandle.get<String>(Screen.QUERY_ARG) } returns query
        every { fetchSearchItems.getPaging() } returns flow {  }

        val viewModel = ResultSearchViewModel(
            savedStateHandle,
            fetchSearchItems,
            searchItemsRepository,
            calculateOffsetPaging
        )

        val jobItemsPagingState = launch(UnconfinedTestDispatcher()){ viewModel.itemsPagingState.collect() }
        val jobUiLogicState = launch(UnconfinedTestDispatcher()){ viewModel.uiLogicState.collect() }

        Assert.assertFalse(viewModel.uiLogicState.value.isLoading)
        Assert.assertNull(viewModel.uiLogicState.value.exception)
        Assert.assertEquals(0, viewModel.itemsPagingState.value.items.size)

        verify {
            savedStateHandle.get<String>(Screen.QUERY_ARG)
        }

        jobUiLogicState.cancel()
        jobItemsPagingState.cancel()
    }

    @Test
    fun collect_ui_state_test() = runTest {
        val query = "query"
        every { savedStateHandle.get<String>(Screen.QUERY_ARG) } returns query

        val searchedItemsPaging = SearchedItemsPaging()
        every { fetchSearchItems.getPaging() } returns flow { emit(searchedItemsPaging) }

        val viewModel = ResultSearchViewModel(
            savedStateHandle,
            fetchSearchItems,
            searchItemsRepository,
            calculateOffsetPaging
        )

        val jobItemsPagingState = launch(UnconfinedTestDispatcher()){ viewModel.itemsPagingState.collect() }
        val jobUiLogicState = launch(UnconfinedTestDispatcher()){ viewModel.uiLogicState.collect() }

        Assert.assertEquals(searchedItemsPaging.items, viewModel.itemsPagingState.value.items)

        verify {
            savedStateHandle.get<String>(Screen.QUERY_ARG)
            fetchSearchItems.getPaging()
        }

        jobItemsPagingState.cancel()
        jobUiLogicState.cancel()
    }

    @Test
    fun on_dismiss_error_test() = runTest {
        val query = "query"
        every { savedStateHandle.get<String>(Screen.QUERY_ARG) } returns query
        every { fetchSearchItems.getPaging() } returns flow {  }

        val viewModel = ResultSearchViewModel(
            savedStateHandle,
            fetchSearchItems,
            searchItemsRepository,
            calculateOffsetPaging
        )

        val jobItemsPagingState = launch(UnconfinedTestDispatcher()){ viewModel.itemsPagingState.collect() }
        val jobUiLogicState = launch(UnconfinedTestDispatcher()){ viewModel.uiLogicState.collect() }

        every { calculateOffsetPaging.nextPaging(any(), any()) } returns 0

        val exception = Exception()
        every { searchItemsRepository.searchItems(any(), any()) } returns flow { emit(Result.Error(exception)) }

        viewModel.onNextPaging(mockk())
        Assert.assertFalse(viewModel.uiLogicState.value.isLoading)
        Assert.assertNotNull(viewModel.uiLogicState.value.exception)
        Assert.assertEquals(0, viewModel.itemsPagingState.value.items.size)

        viewModel.onDismissError()

        Assert.assertFalse(viewModel.uiLogicState.value.isLoading)
        Assert.assertNull(viewModel.uiLogicState.value.exception)
        Assert.assertEquals(0, viewModel.itemsPagingState.value.items.size)

        verify {
            savedStateHandle.get<String>(Screen.QUERY_ARG)
            fetchSearchItems.getPaging()
            calculateOffsetPaging.nextPaging(any(), any())
            searchItemsRepository.searchItems(any(), any())
        }

        jobItemsPagingState.cancel()
        jobUiLogicState.cancel()
    }

    @Test
    fun on_back_paging_test() = runTest {
        val query = "query"
        every { savedStateHandle.get<String>(Screen.QUERY_ARG) } returns query
        every { fetchSearchItems.getPaging() } returns flow {  }

        val viewModel = ResultSearchViewModel(
            savedStateHandle,
            fetchSearchItems,
            searchItemsRepository,
            calculateOffsetPaging
        )

        val jobItemsPagingState = launch(UnconfinedTestDispatcher()){ viewModel.itemsPagingState.collect() }
        val jobUiLogicState = launch(UnconfinedTestDispatcher()){ viewModel.uiLogicState.collect() }

        every { calculateOffsetPaging.backPaging(any()) } returns 0
        every { searchItemsRepository.searchItems(any(), any()) } returns flow { emit(Result.Success(0)) }

        val onSuccess: () -> Unit = mockk()
        every { onSuccess.invoke() } returns Unit
        viewModel.onBackPaging(onSuccess)

        Assert.assertFalse(viewModel.uiLogicState.value.isLoading)
        Assert.assertNull(viewModel.uiLogicState.value.exception)
        Assert.assertEquals(0, viewModel.itemsPagingState.value.items.size)

        verify {
            savedStateHandle.get<String>(Screen.QUERY_ARG)
            fetchSearchItems.getPaging()
            calculateOffsetPaging.backPaging(any())
            searchItemsRepository.searchItems(any(), any())
            onSuccess.invoke()
        }

        jobItemsPagingState.cancel()
        jobUiLogicState.cancel()
    }

    @Test
    fun on_next_paging_test() = runTest {
        val query = "query"
        every { savedStateHandle.get<String>(Screen.QUERY_ARG) } returns query
        every { fetchSearchItems.getPaging() } returns flow {  }

        val viewModel = ResultSearchViewModel(
            savedStateHandle,
            fetchSearchItems,
            searchItemsRepository,
            calculateOffsetPaging
        )

        val jobItemsPagingState = launch(UnconfinedTestDispatcher()){ viewModel.itemsPagingState.collect() }
        val jobUiLogicState = launch(UnconfinedTestDispatcher()){ viewModel.uiLogicState.collect() }

        every { calculateOffsetPaging.nextPaging(any(), any()) } returns 0
        every { searchItemsRepository.searchItems(any(), any()) } returns flow { emit(Result.Success(0)) }

        val onSuccess: () -> Unit = mockk()
        every { onSuccess.invoke() } returns Unit
        viewModel.onNextPaging(onSuccess)

        Assert.assertFalse(viewModel.uiLogicState.value.isLoading)
        Assert.assertNull(viewModel.uiLogicState.value.exception)
        Assert.assertEquals(0, viewModel.itemsPagingState.value.items.size)

        verify {
            savedStateHandle.get<String>(Screen.QUERY_ARG)
            fetchSearchItems.getPaging()
            calculateOffsetPaging.nextPaging(any(), any())
            searchItemsRepository.searchItems(any(), any())
            onSuccess.invoke()
        }

        jobItemsPagingState.cancel()
        jobUiLogicState.cancel()
    }

    @Test
    fun loading_search_items_test() = runTest {
        val query = "query"
        every { savedStateHandle.get<String>(Screen.QUERY_ARG) } returns query
        every { fetchSearchItems.getPaging() } returns flow {  }

        val viewModel = ResultSearchViewModel(
            savedStateHandle,
            fetchSearchItems,
            searchItemsRepository,
            calculateOffsetPaging
        )

        val jobItemsPagingState = launch(UnconfinedTestDispatcher()){ viewModel.itemsPagingState.collect() }
        val jobUiLogicState = launch(UnconfinedTestDispatcher()){ viewModel.uiLogicState.collect() }
        every { searchItemsRepository.searchItems(any(), any()) } returns flow { emit(Result.Loading) }

        viewModel.searchItems(0, mockk())

        Assert.assertTrue(viewModel.uiLogicState.value.isLoading)
        Assert.assertNull(viewModel.uiLogicState.value.exception)
        Assert.assertEquals(0, viewModel.itemsPagingState.value.items.size)

        verify {
            savedStateHandle.get<String>(Screen.QUERY_ARG)
            fetchSearchItems.getPaging()
            searchItemsRepository.searchItems(any(), any())
        }

        jobItemsPagingState.cancel()
        jobUiLogicState.cancel()
    }

    @Test
    fun error_search_items_test() = runTest {
        val query = "query"
        every { savedStateHandle.get<String>(Screen.QUERY_ARG) } returns query
        every { fetchSearchItems.getPaging() } returns flow {  }

        val viewModel = ResultSearchViewModel(
            savedStateHandle,
            fetchSearchItems,
            searchItemsRepository,
            calculateOffsetPaging
        )

        val jobItemsPagingState = launch(UnconfinedTestDispatcher()){ viewModel.itemsPagingState.collect() }
        val jobUiLogicState = launch(UnconfinedTestDispatcher()){ viewModel.uiLogicState.collect() }
        every { searchItemsRepository.searchItems(any(), any()) } returns flow {
            emit(Result.Error(Exception()))
        }

        viewModel.searchItems(0, mockk())

        Assert.assertFalse(viewModel.uiLogicState.value.isLoading)
        Assert.assertNotNull(viewModel.uiLogicState.value.exception)
        Assert.assertEquals(0, viewModel.itemsPagingState.value.items.size)

        verify {
            savedStateHandle.get<String>(Screen.QUERY_ARG)
            fetchSearchItems.getPaging()
            searchItemsRepository.searchItems(any(), any())
        }

        jobItemsPagingState.cancel()
        jobUiLogicState.cancel()
    }

    @Test
    fun success_search_items_test() = runTest {
        val query = "query"
        every { savedStateHandle.get<String>(Screen.QUERY_ARG) } returns query
        every { fetchSearchItems.getPaging() } returns flow {  }

        val viewModel = ResultSearchViewModel(
            savedStateHandle,
            fetchSearchItems,
            searchItemsRepository,
            calculateOffsetPaging
        )

        val jobItemsPagingState = launch(UnconfinedTestDispatcher()){ viewModel.itemsPagingState.collect() }
        val jobUiLogicState = launch(UnconfinedTestDispatcher()){ viewModel.uiLogicState.collect() }
        every { searchItemsRepository.searchItems(any(), any()) } returns flow {
            emit(Result.Success(0))
        }

        val onSuccess: () -> Unit = mockk()
        every { onSuccess.invoke() } returns Unit
        viewModel.searchItems(0, onSuccess)

        Assert.assertFalse(viewModel.uiLogicState.value.isLoading)
        Assert.assertNull(viewModel.uiLogicState.value.exception)
        Assert.assertEquals(0, viewModel.itemsPagingState.value.items.size)

        verify {
            savedStateHandle.get<String>(Screen.QUERY_ARG)
            fetchSearchItems.getPaging()
            searchItemsRepository.searchItems(any(), any())
            onSuccess.invoke()
        }

        jobItemsPagingState.cancel()
        jobUiLogicState.cancel()
    }
}
