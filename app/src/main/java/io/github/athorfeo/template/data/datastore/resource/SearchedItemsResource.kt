package io.github.athorfeo.template.data.datastore.resource

import com.google.gson.annotations.SerializedName
import io.github.athorfeo.template.network.response.ItemSearchItems

data class SearchedItemsResource(
    @SerializedName("total")
    val total: Int,
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("data")
    val data: List<ItemSearchItems>
)
