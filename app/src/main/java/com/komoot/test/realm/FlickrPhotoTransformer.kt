package com.komoot.test.realm

import android.content.Context
import com.komoot.test.R
import com.komoot.test.model.FlickrPhoto
import java.util.*

object FlickrPhotoTransformer {

    fun transformApiItem(
        flickrPhoto: FlickrPhoto,
        context: Context?
    ): RealmFlickrPhoto {
        return RealmFlickrPhoto().apply {
            id = flickrPhoto.id
            url = generatePhotoUrlForPhoto(
                flickrPhoto,
                context
            )
            fetchedAt = Calendar.getInstance().time
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