package com.komoot.test.api

import com.komoot.test.model.FlickrPhotoResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query


interface BaseFlickrApi {

    @GET("rest")
    fun getPhotoByCoordinates(
        @Query("method") method: String = PHOTO_SEARCH_METHOD,
        @Query("api_key") apiKey: String = FLICKR_API_KEY,
        @Query("api_secret") apiSecret: String = FLICKR_API_SECRET,
        @Query("radius") radius: Float = DEFAULT_RADIUS_KM,
        @Query("lat") lat: Float,
        @Query("lon") lon: Float
    ): Single<FlickrPhotoResponse>

    companion object {
        private const val DEFAULT_RADIUS_KM = 0.05f

        private const val PHOTO_SEARCH_METHOD = "flickr.photos.search"

        private const val FLICKR_API_KEY = "ef1c4bbba3ff4eae1bbf916459ffddb1"
        private const val FLICKR_API_SECRET = "5e76cf0b1396da99"
    }
}
