package io.github.athorfeo.template.model

sealed class Result<out R> {
    data class Success<out T>(val data:T): Result<T>()
    data class Error(val exception: Throwable): Result<Nothing>()
    data object Loading: Result<Nothing>()
}
