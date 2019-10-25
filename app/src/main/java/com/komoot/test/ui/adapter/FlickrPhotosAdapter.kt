package com.komoot.test.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.komoot.test.R
import com.komoot.test.util.DraweeImageLoader
import kotlinx.android.synthetic.main.item_flickr_photo.view.*

class FlickrPhotosAdapter(val data: ArrayList<String>?) :
    RecyclerView.Adapter<FlickrPhotosViewHolder>() {

    override fun getItemCount(): Int = data?.size ?: 0

    override fun onBindViewHolder(holder: FlickrPhotosViewHolder, position: Int) {
        val url = data?.getOrNull(position)
        url?.let { holder.populate(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlickrPhotosViewHolder {
        val rowView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_flickr_photo, parent, false)
        return FlickrPhotosViewHolder(rowView)
    }

    fun addItemAndNotify(newUrl: String) {
        data?.add(TOP_POSITION, newUrl)
        notifyItemInserted(TOP_POSITION)
    }

    companion object {

        private const val TOP_POSITION = 0
    }
}

class FlickrPhotosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun populate(photoUrl: String) {

        DraweeImageLoader.loadImage(photoUrl, itemView.flickrPhoto)

    }
}