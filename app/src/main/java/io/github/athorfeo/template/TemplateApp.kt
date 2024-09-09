package io.github.athorfeo.template

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.github.athorfeo.template.network.NetworkConnectionHelper
import javax.inject.Inject

@HiltAndroidApp
class TemplateApp: Application() {
    @Inject lateinit var networkConnectionHelper: NetworkConnectionHelper

    override fun onCreate() {
        super.onCreate()
        networkConnectionHelper.registerNetworkCallback(applicationContext)
    }
}
