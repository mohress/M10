package com.example.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class NetworkMonitor(private val context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.Checking)
    val networkState: StateFlow<NetworkState> = _networkState

    private val client = OkHttpClient.Builder()
        .connectTimeout(5000, java.util.concurrent.TimeUnit.MILLISECONDS)
        .readTimeout(5000, java.util.concurrent.TimeUnit.MILLISECONDS)
        .build()

    private val scope = CoroutineScope(Dispatchers.IO)

    sealed class NetworkState {
        object Checking : NetworkState()
        data class Connected(
            val isWifi: Boolean,
            val signalStrengthDbm: Int?, // signal strength in dbm if available
            val publicIp: String = "Fetching..."
        ) : NetworkState()
        object Offline : NetworkState()
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.d("NetworkMonitor", "Network is available")
            triggerStatusCheck()
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Log.d("NetworkMonitor", "Network is lost")
            _networkState.value = NetworkState.Offline
        }

        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            super.onCapabilitiesChanged(network, capabilities)
            Log.d("NetworkMonitor", "Network capabilities changed")
            triggerStatusCheck()
        }
    }

    init {
        register()
        triggerStatusCheck()
    }

    fun register() {
        try {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            connectivityManager.registerNetworkCallback(request, networkCallback)
        } catch (e: Exception) {
            Log.e("NetworkMonitor", "Error registering callback: ${e.message}")
        }
    }

    fun unregister() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            Log.e("NetworkMonitor", "Error unregistering callback: ${e.message}")
        }
    }

    fun triggerStatusCheck() {
        scope.launch {
            val activeNetwork = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            
            if (activeNetwork == null || capabilities == null) {
                _networkState.value = NetworkState.Offline
                return@launch
            }

            val isWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            val isCellular = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)

            if (!isWifi && !isCellular) {
                _networkState.value = NetworkState.Offline
                return@launch
            }

            // Signal strength if Wi-Fi
            val signalStrength = if (isWifi && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                capabilities.signalStrength
            } else {
                null
            }

            // Set initial connected state
            _networkState.value = NetworkState.Connected(isWifi, signalStrength, "Fetching IP...")

            // Fetch the actual Public IP
            val ip = fetchRealPublicIp()
            _networkState.value = NetworkState.Connected(isWifi, signalStrength, ip)
        }
    }

    private fun fetchRealPublicIp(): String {
        val request = Request.Builder()
            .url("https://api.ipify.org?format=json")
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Android-IsolatedBrowser")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: ""
                    val json = JSONObject(body)
                    json.optString("ip", "Unknown IP")
                } else {
                    "Unknown (Response: ${response.code})"
                }
            }
        } catch (e: IOException) {
            Log.e("NetworkMonitor", "Failed to fetch public IP: ${e.message}")
            "Offline / Unreachable"
        } catch (e: Exception) {
            Log.e("NetworkMonitor", "Error parsing IP: ${e.message}")
            "Error parsing IP"
        }
    }
}
