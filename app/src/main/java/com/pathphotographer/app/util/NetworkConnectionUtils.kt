package com.pathphotographer.app.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import com.pathphotographer.app.PathTrackerApplication
import com.pathphotographer.app.util.MethodUtils.trySilent
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton


object NetworkConnectionUtils {

    fun isNetworkConnected(context: Context?): Boolean {
        val manager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (manager != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                val ni = manager.activeNetworkInfo
                if (ni != null) {
                    return ni.isConnected && (ni.type == ConnectivityManager.TYPE_WIFI || ni.type == ConnectivityManager.TYPE_MOBILE)
                }
            } else {
                val activeNetwork = manager.activeNetwork
                if (activeNetwork != null) {
                    val capabilities = manager.getNetworkCapabilities(activeNetwork)
                    capabilities?.let {
                        return it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || it.hasTransport(
                            NetworkCapabilities.TRANSPORT_WIFI
                        )
                    }
                }
            }
        }
        return false
    }
}

@Singleton
class ConnectionStateMonitor : ConnectivityManager.NetworkCallback(), BaseConnectionStateMonitor {

    private val dispatcher: RxNetworkStateChangeDispatcher = RxNetworkStateChangeDispatcher()

    private val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .build()

    @SuppressLint("MissingPermission")
    override fun enable(context: Context) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(networkRequest, this)
    }

    @SuppressLint("MissingPermission")
    override fun disable(context: Context) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        trySilent { connectivityManager.unregisterNetworkCallback(this) }
    }

    override fun onAvailable(network: Network) {
        dispatcher.setConnectionStatusChanged(true)
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        dispatcher.setConnectionStatusChanged(false)
    }

    override fun onUnavailable() {
        super.onUnavailable()
        dispatcher.setConnectionStatusChanged(false)
    }
}

class RxNetworkStateChangeDispatcher : ContextConsumer {

    @Inject
    override lateinit var applicationContext: Context

    init {
        injectDagger()
    }

    private fun injectDagger() {
        PathTrackerApplication
            .applicationComponent
            .inject(this)
    }

    fun setConnectionStatusChanged(isNetworkConnected: Boolean) {
        subject.onNext(ConnectionState(isNetworkConnected))
    }

    companion object {

        private val subject = PublishSubject.create<ConnectionState>()

        val connectivityChangeObservable: Observable<ConnectionState>
            get() = subject
    }
}

interface BaseConnectionStateMonitor {

    fun enable(context: Context)

    fun disable(context: Context)
}

class ConnectionState(
    val isConnected: Boolean?

//expand with field stating wifi vs. mobile?
)
