package io.github.athorfeo.template.navigation

import androidx.annotation.StringRes
import io.github.athorfeo.template.R

sealed class Screen(val route: String, @StringRes val title: Int? = null) {
    abstract fun buildRoute(): String

    data object QuerySearch: Screen("query_search") {
        override fun buildRoute(): String {
            return route
        }
    }
    data object ResultSearch: Screen("result_search", R.string.title_result_search_screen) {
        override fun buildRoute(): String {
            return "$route/{$QUERY_ARG}"
        }

        fun buildNavigate(query: String): String {
            return "$route/$query"
        }
    }
    data object DetailItem: Screen("item", R.string.title_detail_item_screen) {
        override fun buildRoute(): String {
            return "$route/{$ITEM_ID_ARG}"
        }

        fun buildNavigate(itemId: String): String {
            return "$route/$itemId"
        }
    }

    companion object {
        const val QUERY_ARG = "query"
        const val ITEM_ID_ARG = "item_id"
    }
}

val rootScreens = listOf<Screen>(
    Screen.QuerySearch
)
