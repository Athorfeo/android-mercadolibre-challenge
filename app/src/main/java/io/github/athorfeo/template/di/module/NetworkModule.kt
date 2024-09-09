package io.github.athorfeo.template.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.athorfeo.template.network.api.ItemsSearchesApi
import io.github.athorfeo.template.network.api.buildApi

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    @Provides
    fun providesItemsSearchesApi(): ItemsSearchesApi {
        return buildApi<ItemsSearchesApi>()
    }
}

