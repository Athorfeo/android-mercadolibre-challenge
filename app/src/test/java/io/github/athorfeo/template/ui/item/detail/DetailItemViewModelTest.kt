package io.github.athorfeo.template.ui.item.detail

import androidx.lifecycle.SavedStateHandle
import io.github.athorfeo.template.domain.GetItemInCacheUseCase
import io.github.athorfeo.template.domain.OpenUrlBrowserUseCase
import io.github.athorfeo.template.model.Item
import io.github.athorfeo.template.model.state.ItemState
import io.github.athorfeo.template.navigation.Screen
import io.github.athorfeo.template.util.AppException
import io.github.athorfeo.template.util.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.Before
import org.junit.Rule
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class DetailItemViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var savedStateHandle: SavedStateHandle

    @MockK
    private lateinit var getItemInCacheUseCase: GetItemInCacheUseCase

    @MockK
    private lateinit var openUrlBrowserUseCase: OpenUrlBrowserUseCase

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
        val itemId = "itemId"
        every { savedStateHandle.get<String>(Screen.ITEM_ID_ARG) } returns itemId
        every { getItemInCacheUseCase.getById(itemId) } returns flow {  }

        val viewModel = DetailItemViewModel(savedStateHandle, getItemInCacheUseCase, openUrlBrowserUseCase)
        val job = launch(UnconfinedTestDispatcher()){ viewModel.uiState.collect() }

        Assert.assertFalse(viewModel.uiState.value.isLoading)
        Assert.assertNull(viewModel.uiState.value.exception)
        Assert.assertNull(viewModel.uiState.value.item)

        job.cancel()
    }

    @Test
    fun collect_ui_state_test() = runTest {
        val itemId = "itemId"
        every { savedStateHandle.get<String>(Screen.ITEM_ID_ARG) } returns itemId

        val itemState = ItemState(item = mockk())
        every { getItemInCacheUseCase.getById(itemId) } returns flow { emit(itemState) }

        val viewModel = DetailItemViewModel(savedStateHandle, getItemInCacheUseCase, openUrlBrowserUseCase)
        val job = launch(UnconfinedTestDispatcher()){ viewModel.uiState.collect() }

        Assert.assertEquals(itemState, viewModel.uiState.value)

        job.cancel()

        verify {
            savedStateHandle.get<String>(Screen.ITEM_ID_ARG)
            getItemInCacheUseCase.getById(itemId)
        }
    }

    @Test
    fun open_url_test() = runTest {
        val itemId = "itemId"
        every { savedStateHandle.get<String>(Screen.ITEM_ID_ARG) } returns itemId

        val item: Item = mockk()
        every { item.permalink } returns ""

        val itemState = ItemState(item = item)
        every { getItemInCacheUseCase.getById(itemId) } returns flow { emit(itemState) }
        every { openUrlBrowserUseCase.openUrl(any()) } returns Unit

        val viewModel = DetailItemViewModel(savedStateHandle, getItemInCacheUseCase, openUrlBrowserUseCase)
        val job = launch(UnconfinedTestDispatcher()){ viewModel.uiState.collect() }

        viewModel.onOpenInBrowser()

        job.cancel()

        verify {
            savedStateHandle.get<String>(Screen.ITEM_ID_ARG)
            getItemInCacheUseCase.getById(itemId)
            openUrlBrowserUseCase.openUrl(any())
        }
    }
}
