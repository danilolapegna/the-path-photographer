package com.komoot.test.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.komoot.test.service.FlickrPhotoService
import com.komoot.test.util.SharedPreferenceHelper.clearLastLocationMilestone
import com.komoot.test.util.SharedPreferenceHelper.getLastLocationMilestone
import com.komoot.test.util.SharedPreferenceHelper.hasLastLocationMilestone
import com.komoot.test.util.SharedPreferenceHelper.storeLocationMilestone


object LocationTrackerUtil {

    /*
     * Milestone: either the start location or the last time 100 meters
     * were counted
     */
    fun hasMilestone(context: Context?): Boolean = hasLastLocationMilestone(context)

    fun getLastMilestone(context: Context?) : Pair<Float, Float> = getLastLocationMilestone(context)

    fun setNewMilestone(context: Context?, latLong: Pair<Float, Float>) {
        storeLocationMilestone(context, latLong.first, latLong.second)
    }

    fun clearMilestone(context: Context?) {
        clearLastLocationMilestone(context)
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

    fun startTracking(context: Context) {
        val serviceIntent = getLocationTrackerServiceIntent(context)
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    fun stopTracking(context: Context) {
        val serviceIntent = getLocationTrackerServiceIntent(context)
        context.stopService(serviceIntent)
    }

    private fun getLocationTrackerServiceIntent(context: Context) =
        Intent(context, FlickrPhotoService::class.java)
}