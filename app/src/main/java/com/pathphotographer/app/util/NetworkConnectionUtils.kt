package com.pathphotographer.app.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build


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