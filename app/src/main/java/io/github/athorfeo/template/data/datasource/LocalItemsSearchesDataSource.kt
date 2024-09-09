package io.github.athorfeo.template.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.google.gson.Gson
import io.github.athorfeo.template.data.datastore.SEARCH_ITEMS_STORE
import io.github.athorfeo.template.data.datastore.resource.SearchedItemsResource
import io.github.athorfeo.template.network.response.SearchItemsResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalItemsSearchesDataSource @Inject constructor(
    private val cacheDataStore: DataStore<Preferences>,
    private val serializer: Gson
) {
    suspend fun saveSearchedItems(response: SearchItemsResponse) {
        cacheDataStore.edit { settings ->
            val resource = SearchedItemsResource(
                response.paging.total,
                response.paging.offset,
                response.results
            )
            val json = serializer.toJson(resource)
            settings[SEARCH_ITEMS_STORE] = json
        }
    }

    fun getSearchedItems(): Flow<SearchedItemsResource?> {
        return cacheDataStore.data.map { preferences ->
            preferences[SEARCH_ITEMS_STORE]?.let {
                serializer.fromJson(it, SearchedItemsResource::class.java)
            }
        }
    }
}
