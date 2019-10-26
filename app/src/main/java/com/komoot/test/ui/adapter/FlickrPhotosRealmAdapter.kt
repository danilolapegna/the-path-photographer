package com.komoot.test.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.komoot.test.R
import com.komoot.test.realm.RealmFlickrPhoto
import com.komoot.test.util.DraweeImageLoader
import io.realm.RealmResults
import kotlinx.android.synthetic.main.item_flickr_photo.view.*

class FlickrPhotosRealmAdapter(data: RealmResults<RealmFlickrPhoto>?, listener: RealmAdapterListener? = null) :
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
        photo.url?.let {
            DraweeImageLoader.loadImage(it, itemView.flickrPhoto)
        }
    }
}