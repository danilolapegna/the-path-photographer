package com.pathphotographer.app.util

import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import com.pathphotographer.app.FlickrPhotoRepository
import com.pathphotographer.app.R
import com.pathphotographer.app.model.FlickrPhotoResponse
import com.pathphotographer.app.realm.RealmHelper
import com.pathphotographer.app.service.LocationServiceHandler
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
                if (NetworkConnectionUtils.isNetworkConnected(context)) {
                    val newMilestoneLatitude = location.latitude.toFloat()
                    val newMilestoneLongitude = location.longitude.toFloat()
                    executeFetchPhotoRequest(newMilestoneLatitude, newMilestoneLongitude)
                    SharedPreferenceHelper.storeLocationMilestone(
                        context,
                        newMilestoneLatitude,
                        newMilestoneLongitude
                    )
                } else {
                    Toast.makeText(context, R.string.no_network, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun executeFetchPhotoRequest(
        newMilestoneLatitude: Float,
        newMilestoneLongitude: Float
    ) {
        val request =
            FlickrPhotoRepository.getPhotoByCoordinates(newMilestoneLatitude, newMilestoneLongitude)

        val requestDate = Calendar.getInstance().time
        ReactiveNetworkUtils.executeRequest(request, getRequestListener(requestDate))
    }

    private fun getRequestListener(requestDate: Date) =
        object : RequestListener<FlickrPhotoResponse> {
            override fun onSuccess(response: FlickrPhotoResponse) {
                response.photos?.shuffled()?.firstOrNull()
                    ?.let { photo -> RealmHelper.instance.persistPhoto(requestDate, photo) }
            }

            override fun onFailure(exception: Throwable) {
                Log.e(TAG, "Unable to fetch photo for location")
            }

        }

    companion object {
        private val TAG = FlickrPhotoLocationHandler::class.java.name
    }
}