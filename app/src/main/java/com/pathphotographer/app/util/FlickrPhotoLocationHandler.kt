package com.pathphotographer.app.util

import android.content.Context
import android.location.Location
import android.util.Log
import com.pathphotographer.app.FlickrPhotoRepository
import com.pathphotographer.app.model.FlickrPhotoResponse
import com.pathphotographer.app.realm.RealmHelper
import com.pathphotographer.app.service.LocationServiceHandler
import io.reactivex.disposables.Disposable
import java.util.*

class FlickrPhotoLocationHandler : LocationServiceHandler {

    override fun handleLocation(
        location: Location,
        context: Context
    ) {
        if (!SharedPreferenceHelper.hasLastLocationMilestone(context)) {
            SharedPreferenceHelper.storeLocationMilestone(
                context,
                location.latitude.toFloat(),
                location.longitude.toFloat()
            )
        } else {
            val lastMilestone = SharedPreferenceHelper.getLastLocationMilestone(context)
            if (DistanceUtils.atLeastAHundredMetersBetweenLocations(lastMilestone, location)) {
                val newMilestoneLatitude = location.latitude.toFloat()
                val newMilestoneLongitude = location.longitude.toFloat()
                executeFetchPhotoRequest(
                    newMilestoneLatitude,
                    newMilestoneLongitude,
                    context
                )
                SharedPreferenceHelper.storeLocationMilestone(
                    context,
                    newMilestoneLatitude,
                    newMilestoneLongitude
                )
            }
        }
    }

    fun executeFetchPhotoRequest(
        latitude: Float,
        longitude: Float,
        context: Context?,
        previousItemId: String? = null
    ) {
        if (context != null && shouldAttemptExecuteRequest(previousItemId)) {
            val requestDate = Calendar.getInstance().time

            /*
             * If I have connection, execute request.
             * Otherwise start storing a "mock" item. The UI will check
             * later, on connection restored, if it's needed to be filled
             * with data.
             */
            if (NetworkConnectionUtils.isNetworkConnected(context)) {
                val request =
                    FlickrPhotoRepository.getPhotoByCoordinates(
                        latitude,
                        longitude
                    )

                ReactiveNetworkUtils.executeRequest(
                    request,
                    getRequestListener(
                        requestDate,
                        latitude.toDouble(),
                        longitude.toDouble(),
                        previousItemId
                    )
                )
            } else {
                RealmHelper.instance.persistPhotoItem(
                    requestDate,
                    lat = latitude.toDouble(),
                    lon = longitude.toDouble()
                )
            }
        }
    }

    private fun shouldAttemptExecuteRequest(previousItemId: String?): Boolean =
        previousItemId == null || !progressIds.contains(previousItemId)

    private fun getRequestListener(
        requestDate: Date,
        requestLatitude: Double,
        requestLongitude: Double,
        previousItemId: String? = null
    ) =
        object : RequestListener<FlickrPhotoResponse> {

            override fun onStart(subscription: Disposable) {
                previousItemId?.let { id -> progressIds.add(id) }
            }

            override fun onSuccess(response: FlickrPhotoResponse) {
                response.photos?.shuffled()?.firstOrNull()
                    ?.let { photo ->
                        RealmHelper.instance.persistPhotoItem(
                            requestDate,
                            lat = requestLatitude,
                            lon = requestLongitude,
                            photo = photo,
                            previousItemId = previousItemId
                        )
                    }
                previousItemId?.let { id -> endProgress(id) }
            }

            override fun onFailure(exception: Throwable) {
                Log.e(TAG, "Unable to fetch photo for location")
                previousItemId?.let { id -> endProgress(id) }
            }

            private fun endProgress(previousItemId: String) {
                progressIds.remove(previousItemId)
            }
        }

    companion object {

        /* Keep track of the photos for which I'm trying to fetch info already */
        private val progressIds = ArrayList<String>()

        private val TAG = FlickrPhotoLocationHandler::class.java.name
    }
}