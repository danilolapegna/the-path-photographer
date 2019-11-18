package com.pathphotographer.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pathphotographer.app.R
import com.pathphotographer.app.realm.RealmFlickrPhoto
import com.pathphotographer.app.util.DraweeImageLoader
import com.pathphotographer.app.util.FlickrPhotoLocationHandler
import com.pathphotographer.app.util.NetworkConnectionUtils
import com.pathphotographer.app.util.NetworkConnectionUtils.isNetworkConnected
import io.realm.RealmResults
import kotlinx.android.synthetic.main.item_flickr_photo.view.*

class FlickrPhotosRealmAdapter(
    data: RealmResults<RealmFlickrPhoto>?,
    listener: RealmAdapterListener? = null
) :
    RealmAdapter<FlickrPhotosRealmViewHolder, RealmFlickrPhoto>(data, listener) {

    override fun onBindViewHolder(holder: FlickrPhotosRealmViewHolder, position: Int) {
        val url = data?.getOrNull(position)
        url?.let { holder.populate(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlickrPhotosRealmViewHolder {
        val rowView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_flickr_photo, parent, false)
        return FlickrPhotosRealmViewHolder(rowView)
    }
}

class FlickrPhotosRealmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun populate(photo: RealmFlickrPhoto) {
        if (photo.url.isNullOrEmpty() && isNetworkConnected(itemView.context)) {
            FlickrPhotoLocationHandler().executeFetchPhotoRequest(
                photo.latitude.toFloat(),
                photo.longitude.toFloat(),
                itemView.context,
                photo.id
            )
        }
        DraweeImageLoader.loadImage(photo.url ?: "", itemView.flickrPhoto)
    }
}