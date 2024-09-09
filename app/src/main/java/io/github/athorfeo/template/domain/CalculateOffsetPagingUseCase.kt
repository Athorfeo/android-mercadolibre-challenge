package io.github.athorfeo.template.domain

import io.github.athorfeo.template.data.repository.SearchItemsRepository
import javax.inject.Inject

class CalculateOffsetPagingUseCase @Inject constructor() {
    fun backPaging(currentOffset: Int): Int {
        return currentOffset - SearchItemsRepository.OFFSET_ITEMS_PAGING
    }

    fun nextPaging(currentOffset: Int, total: Int): Int {
        val leftItems = total - currentOffset
        return if (leftItems > SearchItemsRepository.OFFSET_ITEMS_PAGING) {
            currentOffset + SearchItemsRepository.OFFSET_ITEMS_PAGING
        } else {
            currentOffset + leftItems
        }
    }
}
