package io.github.athorfeo.template.model.state

import io.github.athorfeo.template.model.Item
import io.github.athorfeo.template.util.AppException

data class ItemState(
    val isLoading: Boolean = false,
    val exception: AppException? = null,
    val item: Item? = null
)
