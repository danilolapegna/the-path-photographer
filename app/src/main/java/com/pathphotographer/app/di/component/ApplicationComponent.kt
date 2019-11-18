package com.pathphotographer.app.di.component

import android.content.Context
import com.pathphotographer.app.di.module.ActivityModule
import com.pathphotographer.app.di.module.ApplicationModule
import com.pathphotographer.app.di.module.FragmentModule
import com.pathphotographer.app.di.module.ServiceModule
import com.pathphotographer.app.realm.RealmHelper
import com.pathphotographer.app.util.RxNetworkStateChangeDispatcher
import dagger.Component
import javax.inject.Singleton


@Component(modules = [ApplicationModule::class])
@Singleton
interface ApplicationComponent {

    val context: Context

    fun plus(module: FragmentModule): FragmentComponent

    fun plus(module: ActivityModule): ActivityComponent

    fun plus(module: ServiceModule): ServiceComponent

    fun inject(helper: RealmHelper)

    fun inject(dispatcher: RxNetworkStateChangeDispatcher)

}