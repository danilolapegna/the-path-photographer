package com.komoot.test.viewmodel

import com.komoot.test.api.BaseFlickrApi
import com.komoot.test.model.FlickrPhotoResponse
import com.komoot.test.util.FlickrUrlGenerator.generateUrlForPhoto
import io.reactivex.Single

class FlickrPhotoViewModel(val flickrPhotoRepository: FlickrPhotoRepository) {

    fun getFlickrPhotoUrlByCoordinates(
        lat: Float,
        lon: Float
    ): Single<String> {
        return flickrPhotoRepository
            .getPhotoByCoordinates(lat, lon)
            .map { generateUrlForPhoto(it.photos?.firstOrNull()) }
    }
}

class FlickrPhotoRepository(private val flickrPhotoApi: BaseFlickrApi) {

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