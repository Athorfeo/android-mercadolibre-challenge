package io.github.athorfeo.template.network.api

import io.github.athorfeo.template.network.response.SearchItemsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ItemsSearchesApi {
    @GET("sites/MLA/search")
    suspend fun fetchSearchItems(
        @Query("q") query: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): Response<SearchItemsResponse>
}


