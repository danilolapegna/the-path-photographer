package com.pathphotographer.app.di.module

import com.pathphotographer.app.util.BaseTrackerHelper
import com.pathphotographer.app.util.LocationTrackerHelper
import dagger.Module
import dagger.Provides


@Module
class TrackerContextModule {

    @Provides
    internal fun provideTracker(): BaseTrackerHelper = LocationTrackerHelper()
}