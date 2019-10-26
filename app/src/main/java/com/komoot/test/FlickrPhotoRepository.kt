package com.komoot.test

import com.komoot.test.api.BaseFlickrApi
import com.komoot.test.client.BaseFlickrApiClient
import com.komoot.test.model.FlickrPhotoResponse
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