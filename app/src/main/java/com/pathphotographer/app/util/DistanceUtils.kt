package com.pathphotographer.app.util

import android.location.Location

object DistanceUtils {

    private const val THRESHOLD_IN_METERS = 100

    fun atLeastAHundredMetersBetweenLocations(
        lastMilestone: Pair<Float, Float>,
        lastRetrievedLocation: Location
    ): Boolean {
        return getMeterDistanceBetweenPoints(
            lastMilestone.first.toDouble(),
            lastMilestone.second.toDouble(),
            lastRetrievedLocation.latitude,
            lastRetrievedLocation.longitude
        ) >= THRESHOLD_IN_METERS
    }

    private fun getMeterDistanceBetweenPoints(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double
    ): Float {
        val results = floatArrayOf(0F)
        Location.distanceBetween(
            startLatitude,
            startLongitude,
            endLatitude,
            endLongitude,
            results
        )
        return results[0]
    }
}