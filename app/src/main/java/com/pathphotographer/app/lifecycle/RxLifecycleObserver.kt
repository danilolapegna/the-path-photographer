package com.pathphotographer.app.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class RxLifecycleObserver(private val callback: RxUI) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroyUI() {
        callback.disposeSubscriptions()
    }
}

interface RxUI {

    fun disposeSubscriptions()
}