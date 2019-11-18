package com.pathphotographer.app.di.component

import com.pathphotographer.app.di.module.ServiceModule
import com.pathphotographer.app.di.scope.ServiceScope
import com.pathphotographer.app.service.LocationService
import dagger.Subcomponent

@Subcomponent(modules = [ServiceModule::class])
@ServiceScope
interface ServiceComponent {

    fun inject(locationService: LocationService)
}