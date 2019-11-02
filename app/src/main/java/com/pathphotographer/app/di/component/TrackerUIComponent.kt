package com.pathphotographer.app.di.component

import com.pathphotographer.app.di.module.TrackerContextModule
import com.pathphotographer.app.service.LocationService
import com.pathphotographer.app.ui.activity.MainActivity
import com.pathphotographer.app.ui.fragment.MainFragment
import dagger.Component
import javax.inject.Singleton

@Component(modules = [TrackerContextModule::class])
interface TrackerUIComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(mainFragment: MainFragment)

    fun inject(locationService: LocationService)
}