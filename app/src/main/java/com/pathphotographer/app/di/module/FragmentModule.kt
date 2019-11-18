package com.pathphotographer.app.di.module

import androidx.lifecycle.ViewModelProvider
import com.pathphotographer.app.di.scope.FragmentScope
import com.pathphotographer.app.ui.fragment.MainFragment
import com.pathphotographer.app.util.BaseConnectionStateMonitor
import com.pathphotographer.app.util.BaseTrackerHelper
import com.pathphotographer.app.util.ConnectionStateMonitor
import com.pathphotographer.app.util.LocationTrackerHelper
import com.pathphotographer.app.viewmodel.FlickrPhotosViewModel
import dagger.Module
import dagger.Provides


@Module
class FragmentModule(val fragment: MainFragment) {

    @Provides
    @FragmentScope
    internal fun provideTracker(): BaseTrackerHelper = LocationTrackerHelper()

    @Provides
    @FragmentScope
    internal fun provideNetworkMonitor(): BaseConnectionStateMonitor = ConnectionStateMonitor()

    @Provides
    @FragmentScope
    internal fun provideViewModel(): FlickrPhotosViewModel =
        ViewModelProvider(fragment).get(FlickrPhotosViewModel::class.java)

}