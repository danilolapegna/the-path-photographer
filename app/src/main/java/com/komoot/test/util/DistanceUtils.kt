package com.komoot.test.util

import android.location.Location

object DistanceUtils {

    fun getMeterDistanceBetweenPoints(startLatitude: Double,
                                      startLongitude: Double,
                                      endLatitude: Double,
                                      endLongitude: Double): Float {
        val results = floatArrayOf()
        Location.distanceBetween(
            startLatitude,
            startLongitude,
            endLatitude,
            endLongitude,
            results)
        return results[0]
    }
}