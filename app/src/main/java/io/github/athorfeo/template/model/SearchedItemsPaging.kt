package io.github.athorfeo.template.model

import io.github.athorfeo.template.data.repository.SearchItemsRepository


data class SearchedItemsPaging(
    val total: Int = 0,
    val offset: Int = SearchItemsRepository.OFFSET_ITEMS_PAGING,
    val items: List<Item> = listOf()
)
