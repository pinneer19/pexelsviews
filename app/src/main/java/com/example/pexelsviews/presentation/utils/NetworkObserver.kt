package com.example.pexelsviews.presentation.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ConnectivityObserver {
    fun observe(): Flow<Status>

    enum class Status {
        LOST, AVAILABLE
    }
}

class NetworkObserver @Inject constructor(
    private val context: Context
) : ConnectivityObserver {

    private val manager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun observe(): Flow<ConnectivityObserver.Status> {
        return callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    launch { send(ConnectivityObserver.Status.AVAILABLE) }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    launch { send(ConnectivityObserver.Status.LOST) }
                }
            }
            manager.registerDefaultNetworkCallback(callback)

            awaitClose {
                manager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()
    }
}