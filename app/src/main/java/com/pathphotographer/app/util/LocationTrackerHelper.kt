package com.pathphotographer.app.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.pathphotographer.app.service.LocationService
import com.pathphotographer.app.util.SharedPreferenceHelper.clearLastLocationMilestone
import io.reactivex.Observable
import io.reactivex.subjects.ReplaySubject
import javax.inject.Singleton

@Singleton
interface BaseTrackerHelper {

    val isStartedOrStartingTracking: Boolean

    val trackingObservable: Observable<Boolean>

    fun startTracking(context: Context, startService: Boolean = true)

    fun stopTracking(context: Context, stopService: Boolean = true)

    fun getLocationPermissions(): Array<String> =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.FOREGROUND_SERVICE
            )
        }
}

@Singleton
class LocationTrackerHelper : BaseTrackerHelper {

    /*
     * You can subscribe external components to this, when you want to monitor whether the
     * tracking is in progress. It works as a bus
     */
    override val trackingObservable: ReplaySubject<Boolean> = ReplaySubject.create<Boolean>()

    override var isStartedOrStartingTracking: Boolean
        get() = LocationService.isStartedOrStarting
        set(value) {
            LocationService.isStartedOrStarting = value
        }

    private val flickrLocationServiceHandler: FlickrPhotoLocationHandler by lazy { FlickrPhotoLocationHandler() }

    override fun startTracking(context: Context, startService: Boolean) {
        trackingObservable.onNext(true)
        if (startService) {
            isStartedOrStartingTracking = true
            LocationService.addHandler(flickrLocationServiceHandler)
            val serviceIntent = getLocationTrackerServiceIntent(context)
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }

    override fun stopTracking(context: Context, stopService: Boolean) {
        trackingObservable.onNext(false)
        clearLastLocationMilestone(context)
        if (stopService) {
            isStartedOrStartingTracking = false
            LocationService.clearAllHandlers()
            val serviceIntent = getLocationTrackerServiceIntent(context)
            context.stopService(serviceIntent)
        }
    }

    private fun getLocationTrackerServiceIntent(context: Context) =
        Intent(context, LocationService::class.java)
}