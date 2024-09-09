package io.github.athorfeo.template.data.repository

import dagger.hilt.android.scopes.ViewModelScoped
import io.github.athorfeo.template.data.datasource.LocalItemsSearchesDataSource
import io.github.athorfeo.template.data.datasource.NetworkItemsSearchesDataSource
import io.github.athorfeo.template.data.datastore.resource.SearchedItemsResource
import io.github.athorfeo.template.model.Result
import io.github.athorfeo.template.network.response.ItemSearchItems
import io.github.athorfeo.template.util.AppException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@ViewModelScoped
class SearchItemsRepository @Inject constructor(
    private val networkItemsSearchesDataSource: NetworkItemsSearchesDataSource,
    private val localItemsSearchesDataSource: LocalItemsSearchesDataSource
) {
    fun searchItems(query: String, offset: Int): Flow<Result<Int>> {
        return flow {
            emit(Result.Loading)
            val response = networkItemsSearchesDataSource.fetchSearchItems(
                query,
                offset,
                LIMIT_ITEMS_PAGING
            )
            localItemsSearchesDataSource.saveSearchedItems(response)
            emit(Result.Success(response.results.size))
        }.catch {
            emit(Result.Error(it))
        }
    }

    fun getSearchedItems(): Flow<SearchedItemsResource> {
        return localItemsSearchesDataSource
            .getSearchedItems()
            .map { it ?: SearchedItemsResource(0, 0, listOf()) }
            .catch {
                emit(SearchedItemsResource(0, 0, listOf()))
            }
    }

    fun getItemInCache(itemId: String): Flow<Result<ItemSearchItems>> {
        return localItemsSearchesDataSource
            .getSearchedItems()
            .map { resource ->
                resource?.data?.find{ it.id == itemId }?.let { itemFound ->
                    Result.Success(itemFound)
                } ?: run {
                    val exception = AppException()
                    Result.Error(exception)
                }
            }
            .onStart { emit(Result.Loading) }
            .catch { emit(Result.Error(it)) }
    }

    companion object {
        const val LIMIT_ITEMS_PAGING = 25
        const val OFFSET_ITEMS_PAGING = 25
        const val START_OFFSET_ITEMS_PAGING = 0
    }
}
