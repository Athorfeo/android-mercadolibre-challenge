package io.github.athorfeo.template.data.datasource

import io.github.athorfeo.template.network.NetworkExecutor
import io.github.athorfeo.template.network.api.ItemsSearchesApi
import io.github.athorfeo.template.network.response.SearchItemsResponse
import javax.inject.Inject

class NetworkItemsSearchesDataSource @Inject constructor(
    private val networkExecutor: NetworkExecutor,
    private val api: ItemsSearchesApi
) {
    suspend fun fetchSearchItems(query: String, offset: Int, limit: Int): SearchItemsResponse {
        return checkNotNull(networkExecutor.fetch {
            api.fetchSearchItems(query, offset, limit)
        })
    }
}
