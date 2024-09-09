package io.github.athorfeo.template.model.state

import io.github.athorfeo.template.util.AppException

data class UiLogicState(
    val isLoading: Boolean = false,
    val exception: AppException? = null
)
