package io.github.athorfeo.template.ui.search.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.athorfeo.template.R
import io.github.athorfeo.template.data.repository.SearchItemsRepository
import io.github.athorfeo.template.domain.CalculateOffsetPagingUseCase
import io.github.athorfeo.template.domain.GetSearchedItemsUseCase
import io.github.athorfeo.template.model.Result
import io.github.athorfeo.template.model.SearchedItemsPaging
import io.github.athorfeo.template.model.state.UiLogicState
import io.github.athorfeo.template.navigation.Screen
import io.github.athorfeo.template.util.AppException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultSearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getSearchedItems: GetSearchedItemsUseCase,
    private val searchItemsRepository: SearchItemsRepository,
    private val calculateOffsetPaging: CalculateOffsetPagingUseCase
): ViewModel() {
    val query: String = checkNotNull(savedStateHandle[Screen.QUERY_ARG])

    private val _uiLogicState = MutableStateFlow(UiLogicState())
    val uiLogicState: StateFlow<UiLogicState> = _uiLogicState

    val itemsPagingState: StateFlow<SearchedItemsPaging> = getSearchedItems
        .getPaging()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchedItemsPaging()
        )

    fun onDismissError() {
        _uiLogicState.update { it.copy(isLoading = false, exception = null) }
    }

    fun onBackPaging() {
        val offset = calculateOffsetPaging.backPaging(itemsPagingState.value.offset)
        searchItems(offset)
    }

    fun onNextPaging() {
        val offset = calculateOffsetPaging.nextPaging(
            itemsPagingState.value.offset,
            itemsPagingState.value.total
        )
        searchItems(offset)
    }

    fun searchItems(offset: Int) {
        viewModelScope.launch {
            searchItemsRepository.searchItems(query, offset).collect { result ->
                when(result) {
                    is Result.Loading -> {
                        _uiLogicState.update { it.copy(isLoading = true) }
                    }
                    is Result.Error -> {
                        val exception = AppException(cause = result.exception)
                        _uiLogicState.update { it.copy(isLoading = false, exception = exception) }
                    }
                    is Result.Success -> {
                        _uiLogicState.update { it.copy(isLoading = false, exception = null) }
                    }
                }
            }
        }
    }
}
