package io.github.athorfeo.template.di.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.athorfeo.template.util.AppLogger
import io.github.athorfeo.template.util.Logger
import io.github.athorfeo.template.data.datastore.cacheDataStore
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object CommonsModule {
    @Provides
    fun providesLogger(): Logger {
        return AppLogger
    }

    @Singleton
    @Provides
    fun providesGson(): Gson {
        return Gson()
    }

    @Provides
    fun providesCacheDataStore(
        @ApplicationContext applicationContext: Context
    ): DataStore<Preferences> {
        return applicationContext.cacheDataStore
    }
}
