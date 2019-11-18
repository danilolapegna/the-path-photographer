package com.pathphotographer.app.realm

import android.content.Context
import com.pathphotographer.app.R
import com.pathphotographer.app.model.FlickrPhoto
import com.pathphotographer.app.util.IdUtils.generateId
import java.util.*

object FlickrPhotoTransformer {

    fun generateRealmItem(
        requestTime: Date,
        context: Context?,
        lat: Double,
        lon: Double,
        flickrPhoto: FlickrPhoto?,
        previousItemId: String? = null
    ): RealmFlickrPhoto {
        return RealmFlickrPhoto().apply {
            id = previousItemId ?: generateId()
            flickrPhoto?.let {
                photoId = it.id
                url = generatePhotoUrlForPhoto(it, context)
                latitude = lat
                longitude = lon
            }
            fetchedAt = requestTime
        }
    }

    private fun generatePhotoUrlForPhoto(flickrPhoto: FlickrPhoto, context: Context?): String {
        val baseString = context?.getString(R.string.flickr_url_base)
        baseString?.let {
            return String.format(
                baseString,
                flickrPhoto.farm,
                flickrPhoto.server,
                flickrPhoto.id,
                flickrPhoto.secret
            )
        }
        return ""
    }
}