package io.github.athorfeo.template.model

data class Item(
    val id: String,
    val title: String,
    val permalink: String,
    val thumbnail: String,
    val currencyId: String,
    val price: Double,
    val salePrice: SalePriceItem,
    val availableQuantity: Int
)
