package com.pathphotographer.app.lifecycle

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.pathphotographer.app.util.ConnectionState
import com.pathphotographer.app.util.RxNetworkStateChangeDispatcher.Companion.connectivityChangeObservable
import io.reactivex.disposables.Disposable

class NetworkChangeLifecycleObserver(private var callback: NetworkChangeCallbackUI) :
    LifecycleObserver {

    private var networkDisposable: Disposable? = null

    @SuppressLint("CheckResult")
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun executeAction() {
        networkDisposable =
            connectivityChangeObservable.subscribe { callback.onConnectionChange(it) }
    }

    @SuppressLint("CheckResult")
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun unsubscribe() {
        networkDisposable?.dispose()
        networkDisposable = null
    }
}

interface NetworkChangeCallbackUI {

    fun onConnectionChange(connectionState: ConnectionState)
}
