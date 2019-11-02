package com.pathphotographer.app

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.pathphotographer.app.di.component.ApplicationComponent
import com.pathphotographer.app.di.component.DaggerApplicationComponent
import com.pathphotographer.app.di.module.ApplicationModule
import io.realm.Realm

class PathTrackerApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
        Realm.init(this)
        initDaggerApplicationComponent()
    }

    private fun initDaggerApplicationComponent() {
        applicationComponent = DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .build()
        applicationComponent.inject(this)
    }

    companion object {

        lateinit var applicationComponent: ApplicationComponent

        fun getComponent() = applicationComponent
    }
}