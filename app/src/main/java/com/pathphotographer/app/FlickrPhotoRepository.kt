package com.pathphotographer.app

import com.pathphotographer.app.api.BaseFlickrApi
import com.pathphotographer.app.client.BaseFlickrApiClient
import com.pathphotographer.app.model.FlickrPhotoResponse
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