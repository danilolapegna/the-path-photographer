package com.pathphotographer.app.di.component

import android.content.Context
import com.pathphotographer.app.PathTrackerApplication
import com.pathphotographer.app.di.module.ApplicationModule
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    val context: Context

    fun inject(any: Any)

}