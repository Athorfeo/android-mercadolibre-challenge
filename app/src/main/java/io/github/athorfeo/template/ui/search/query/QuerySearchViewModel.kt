package io.github.athorfeo.template.ui.search.query

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.athorfeo.template.R
import io.github.athorfeo.template.data.repository.SearchItemsRepository
import io.github.athorfeo.template.model.Result
import io.github.athorfeo.template.model.state.UiLogicState
import io.github.athorfeo.template.util.AppException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuerySearchViewModel @Inject constructor(
    private val searchItemsRepository: SearchItemsRepository
): ViewModel() {
    private val _uiLogicState = MutableStateFlow(UiLogicState())
    val uiLogicState: StateFlow<UiLogicState> = _uiLogicState

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    fun onDismissError() {
        _uiLogicState.update { it.copy(isLoading = false, exception = null) }
    }

    fun onQueryChange(text: String) {
        _query.update { text }
    }

    fun onSearch(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            searchItemsRepository
                .searchItems(query.value, SearchItemsRepository.START_OFFSET_ITEMS_PAGING)
                .collect { result ->
                    when(result) {
                        is Result.Loading -> {
                            _uiLogicState.update { it.copy(isLoading = true) }
                        }
                        is Result.Error -> {
                            val exception = AppException(cause = result.exception)
                            _uiLogicState.update { it.copy(isLoading = false, exception = exception) }
                        }
                        is Result.Success -> {
                            val itemsSize = result.data
                            if(itemsSize > 0) {
                                _uiLogicState.update { it.copy(isLoading = false, exception = null) }
                                onSuccess(query.value)
                            } else {
                                val exception = AppException(
                                    R.string.title_not_found_item_search_dialog,
                                    R.string.text_not_found_item_search_dialog
                                )
                                _uiLogicState.update { it.copy(isLoading = false, exception = exception) }
                            }
                        }
                    }
                }
        }
    }
}
