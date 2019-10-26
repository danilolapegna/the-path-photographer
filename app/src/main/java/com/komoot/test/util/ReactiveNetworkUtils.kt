package com.komoot.test.util

import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

object ReactiveNetworkUtils {

    fun <T> executeRequest(request: Single<T>, listener: RequestListener<T>) {
        request.subscribeOn(Schedulers.newThread())
            .cache()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(wrapObserver(listener))
    }

    private fun <T> wrapObserver(listener: RequestListener<T>) = object : SingleObserver<T> {

        override fun onSuccess(t: T) {
            listener.onSuccess(t)
        }

        override fun onError(e: Throwable) {
            listener.onFailure(e)
        }

        override fun onSubscribe(d: Disposable) {}
    }
}

interface RequestListener<T> {

    fun onSuccess(response: T)

    fun onFailure(exception: Throwable)
}