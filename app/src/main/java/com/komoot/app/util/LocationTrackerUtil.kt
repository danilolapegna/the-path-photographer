package com.komoot.app.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.komoot.app.service.LocationService
import com.komoot.app.util.SharedPreferenceHelper.clearLastLocationMilestone
import com.komoot.app.util.SharedPreferenceHelper.getLastLocationMilestone
import com.komoot.app.util.SharedPreferenceHelper.hasLastLocationMilestone
import com.komoot.app.util.SharedPreferenceHelper.storeLocationMilestone
import io.reactivex.subjects.PublishSubject


object LocationTrackerUtil {

    /*
     * You can subscribe external components to this, when you want to monitor whether the
     * tracking is in progress. It works as a bus
     */
    val trackingSubject: PublishSubject<Boolean> = PublishSubject.create<Boolean>()

    private val flickrLocationServiceHandler: FlickrPhotoLocationHandler by lazy { FlickrPhotoLocationHandler() }

    /*
     * Milestone: either the start location or the last time 100 meters
     * were counted
     */
    fun hasMilestone(context: Context?): Boolean = hasLastLocationMilestone(context)

    fun getLastMilestone(context: Context?): Pair<Float, Float> = getLastLocationMilestone(context)

    fun setNewMilestone(context: Context?, latLong: Pair<Float, Float>) {
        storeLocationMilestone(context, latLong.first, latLong.second)
    }

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
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }

    fun startTracking(context: Context, startService: Boolean = true) {
        LocationService.isStartedOrStarting = true
        LocationService.addHandler(flickrLocationServiceHandler)
        trackingSubject.onNext(true)
        if (startService) {
            val serviceIntent = getLocationTrackerServiceIntent(context)
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }

    fun stopTracking(context: Context, stopService: Boolean = true) {
        LocationService.isStartedOrStarting = false
        LocationService.clearAllHanders()
        trackingSubject.onNext(false)
        clearLastLocationMilestone(context)
        if (stopService) {
            val serviceIntent = getLocationTrackerServiceIntent(context)
            context.stopService(serviceIntent)
        }
    }

    private fun getLocationTrackerServiceIntent(context: Context) =
        Intent(context, LocationService::class.java)
}