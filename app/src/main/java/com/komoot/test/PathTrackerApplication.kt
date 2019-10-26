package com.komoot.test

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import io.realm.Realm

class PathTrackerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
        Realm.init(this)
        instance = this
    }

    override fun onTerminate() {
        super.onTerminate()
        instance = null
    }

    companion object {

        var instance: Application? = null
    }
}