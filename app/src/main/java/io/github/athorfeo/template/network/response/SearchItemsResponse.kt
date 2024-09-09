package io.github.athorfeo.template.network.response

import com.google.gson.annotations.SerializedName
import io.github.athorfeo.template.model.Item
import io.github.athorfeo.template.model.SalePriceItem

data class SearchItemsResponse(
    @SerializedName("paging")
    val paging: PagingSearchItems,
    @SerializedName("results")
    val results: List<ItemSearchItems>
)

data class PagingSearchItems(
    @SerializedName("total")
    val total: Int,
    @SerializedName("primary_results")
    val primaryResults: Int,
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("limit")
    val limit: Int
)

data class ItemSearchItems(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("permalink")
    val permalink: String,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("currency_id")
    val currencyId: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("sale_price")
    val salePrice: SalePriceResultSearchItems,
    @SerializedName("available_quantity")
    val availableQuantity: Int
)

fun ItemSearchItems.toDomainModel(): Item {
    return Item(id, title, permalink, thumbnail, currencyId, price, salePrice.toDomainModel(), availableQuantity)
}

data class SalePriceResultSearchItems(
    @SerializedName("currency_id")
    val currencyId: String,
    @SerializedName("amount")
    val amount: Double
)

fun SalePriceResultSearchItems.toDomainModel(): SalePriceItem {
    return SalePriceItem(currencyId, amount)
}
