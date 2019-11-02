package com.pathphotographer.app.realm

import android.content.Context
import com.pathphotographer.app.R
import com.pathphotographer.app.model.FlickrPhoto
import java.util.*

object FlickrPhotoTransformer {

    fun transformApiItem(
        requestTime: Date,
        flickrPhoto: FlickrPhoto,
        context: Context?
    ): RealmFlickrPhoto {
        return RealmFlickrPhoto().apply {
            id = flickrPhoto.id
            url = generatePhotoUrlForPhoto(
                flickrPhoto,
                context
            )
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