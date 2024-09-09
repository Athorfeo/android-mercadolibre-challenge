package io.github.athorfeo.template.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.cacheDataStore by preferencesDataStore("cache_datastore")
val SEARCH_ITEMS_STORE = stringPreferencesKey("search_items_store")
