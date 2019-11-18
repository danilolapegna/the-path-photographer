package com.pathphotographer.app.di.module

import com.pathphotographer.app.di.scope.ActivityScope
import com.pathphotographer.app.util.BaseConnectionStateMonitor
import com.pathphotographer.app.util.BaseTrackerHelper
import com.pathphotographer.app.util.ConnectionStateMonitor
import com.pathphotographer.app.util.LocationTrackerHelper
import dagger.Module
import dagger.Provides


@Module
class ActivityModule {

    @Provides
    @ActivityScope
    internal fun provideTracker(): BaseTrackerHelper = LocationTrackerHelper()

    @Provides
    @ActivityScope
    internal fun provideNetworkMonitor(): BaseConnectionStateMonitor = ConnectionStateMonitor()
}