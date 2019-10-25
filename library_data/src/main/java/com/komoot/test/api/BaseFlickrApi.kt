package com.komoot.test.api

import com.komoot.test.model.FlickrPhotoResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query


interface BaseFlickrApi {

    @GET
    fun getPhotoByCoordinates(
        @Query("method") method: String = PHOTO_SEARCH_METHOD,
        @Query("lat") lat: Float,
        @Query("lon") lon: Float
    ): Single<FlickrPhotoResponse>

    companion object {
        private const val PHOTO_SEARCH_METHOD = "flickr.photos.search"
    }
}
