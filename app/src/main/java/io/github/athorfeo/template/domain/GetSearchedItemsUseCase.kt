package io.github.athorfeo.template.domain

import io.github.athorfeo.template.data.repository.SearchItemsRepository
import io.github.athorfeo.template.model.SearchedItemsPaging
import io.github.athorfeo.template.network.response.toDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSearchedItemsUseCase @Inject constructor(
    private val searchItemsRepository: SearchItemsRepository
) {
    fun getPaging(): Flow<SearchedItemsPaging> {
        return searchItemsRepository
            .getSearchedItems()
            .map { resource ->
                SearchedItemsPaging(
                    resource.total,
                    resource.offset,
                    resource.data.map { it.toDomainModel() }
                )
            }
    }
}
