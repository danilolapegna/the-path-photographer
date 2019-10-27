package com.komoot.app

import com.komoot.app.api.BaseFlickrApi
import com.komoot.app.client.BaseFlickrApiClient
import com.komoot.app.model.FlickrPhotoResponse
import io.reactivex.Single

object FlickrPhotoRepository {

    private val flickrApiClient: BaseFlickrApiClient by lazy { BaseFlickrApiClient() }

    private val flickrPhotoApi: BaseFlickrApi by lazy { flickrApiClient.getFlickrService() }

    fun getPhotoByCoordinates(
        lat: Float,
        lon: Float
    ): Single<FlickrPhotoResponse> {
        return flickrPhotoApi.getPhotoByCoordinates(
            lat = lat,
            lon = lon
        )
    }
}