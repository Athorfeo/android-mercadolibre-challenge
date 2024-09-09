package io.github.athorfeo.template.ui.item.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.athorfeo.template.data.repository.SearchItemsRepository
import io.github.athorfeo.template.domain.GetItemInCacheUseCase
import io.github.athorfeo.template.domain.OpenUrlBrowserUseCase
import io.github.athorfeo.template.model.Result
import io.github.athorfeo.template.model.state.ItemState
import io.github.athorfeo.template.navigation.Screen
import io.github.athorfeo.template.network.response.toDomainModel
import io.github.athorfeo.template.util.AppException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DetailItemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getItemInCacheUseCase: GetItemInCacheUseCase,
    private val openUrlBrowser: OpenUrlBrowserUseCase
): ViewModel() {
    private val itemId: String = checkNotNull(savedStateHandle[Screen.ITEM_ID_ARG])
    val uiState: StateFlow<ItemState> = getItemInCacheUseCase.getById(itemId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ItemState()
        )

    fun onOpenInBrowser() {
        uiState.value.item?.let {
            openUrlBrowser.openUrl(it.permalink)
        }
    }
}
