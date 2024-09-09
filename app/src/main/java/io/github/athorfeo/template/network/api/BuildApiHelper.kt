package io.github.athorfeo.template.network.api

import io.github.athorfeo.template.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

inline fun <reified T : Any> buildApi(): T {
    val retrofit = Retrofit.Builder()
        .client(buildHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BuildConfig.BASE_URL)
        .build()

    return retrofit.create(T::class.java)
}

fun buildHttpClient(
    isDebug: Boolean = true
): OkHttpClient {
    return OkHttpClient.Builder()
        .apply {
            if (isDebug) {
                addInterceptor(
                    HttpLoggingInterceptor().apply {
                        this.level = HttpLoggingInterceptor.Level.BODY
                    }
                )
            }
        }
        .build()
}
