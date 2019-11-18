package com.pathphotographer.app.util

import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

object ReactiveNetworkUtils {

    private const val TIMES_RETRY = 3L

    fun <T> executeRequest(request: Single<T>, listener: RequestListener<T>) {
        request.subscribeOn(Schedulers.newThread())
            .cache()
            .retry(TIMES_RETRY)
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

        override fun onSubscribe(subscription: Disposable) {
            listener.onStart(subscription)
        }
    }
}

interface RequestListener<T> {

    fun onStart(subscription: Disposable)

    fun onSuccess(response: T)

    fun onFailure(exception: Throwable)
}