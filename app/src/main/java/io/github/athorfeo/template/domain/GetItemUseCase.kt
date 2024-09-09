package io.github.athorfeo.template.domain

import io.github.athorfeo.template.data.repository.SearchItemsRepository
import io.github.athorfeo.template.model.Result
import io.github.athorfeo.template.model.state.ItemState
import io.github.athorfeo.template.network.response.toDomainModel
import io.github.athorfeo.template.util.AppException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetItemUseCase @Inject constructor(
    private val searchItemsRepository: SearchItemsRepository
) {
    fun getById(itemId: String): Flow<ItemState> {
        return searchItemsRepository.getItem(itemId).map { result ->
            when(result) {
                is Result.Loading -> {
                    ItemState(isLoading = true)
                }
                is Result.Error -> {
                    val exception = AppException(cause = result.exception)
                    ItemState(isLoading = false, exception = exception)
                }
                is Result.Success -> {
                    ItemState(item = result.data.toDomainModel())
                }
            }
        }
    }
}
