package com.pathphotographer.app

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.pathphotographer.app.di.component.*
import com.pathphotographer.app.di.module.ActivityModule
import com.pathphotographer.app.di.module.ApplicationModule
import com.pathphotographer.app.di.module.FragmentModule
import com.pathphotographer.app.di.module.ServiceModule
import com.pathphotographer.app.service.LocationService
import com.pathphotographer.app.ui.fragment.MainFragment
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
    }

    companion object {

        lateinit var applicationComponent: ApplicationComponent

        private var activityComponent: ActivityComponent? = null

        private var fragmentComponent: FragmentComponent? = null

        private var serviceComponent: ServiceComponent? = null

        fun initActivityComponent(): ActivityComponent {
            if (activityComponent == null) {
                activityComponent = applicationComponent.plus(ActivityModule())
            }
            return activityComponent!!
        }

        fun initFragmentComponent(fragment: MainFragment): FragmentComponent {
            if (fragmentComponent == null) {
                fragmentComponent = applicationComponent.plus(FragmentModule(fragment))
            }
            return fragmentComponent!!
        }

        fun initServiceComponent(service: LocationService): ServiceComponent {
            if (serviceComponent == null) {
                serviceComponent = applicationComponent.plus(ServiceModule(service))
            }
            return serviceComponent!!
        }
    }
}