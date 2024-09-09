package io.github.athorfeo.template.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import io.github.athorfeo.template.util.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConnectionHelper @Inject constructor(
    private val logger: Logger
) {
    var hasNetworkConnection = false
        private set

    fun registerNetworkCallback(context: Context) {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)?.let {
            it.registerNetworkCallback(getNetworkRequest(), getNetworkCallback())
            logger.d("$TAG: Network callback registered!")
        } ?: run {
            logger.e("$TAG: Error registering network callback!")
        }
    }

    fun getNetworkCallback(): ConnectivityManager.NetworkCallback {
        return object: ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                hasNetworkConnection = true
                logger.d("$TAG: hasNetworkConnection: $hasNetworkConnection")
            }

            override fun onUnavailable() {
                super.onUnavailable()
                hasNetworkConnection = false
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                hasNetworkConnection = false
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                hasNetworkConnection = false
                logger.d("$TAG: hasNetworkConnection: $hasNetworkConnection")
            }
        }
    }

    fun getNetworkRequest(): NetworkRequest {
        return NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
    }

    companion object {
        const val TAG = "NetworkConnectionUtil"
    }
}
