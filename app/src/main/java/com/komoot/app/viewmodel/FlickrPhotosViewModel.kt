package com.komoot.app.viewmodel

import androidx.lifecycle.ViewModel
import com.komoot.app.realm.RealmFlickrPhoto
import com.komoot.app.realm.RealmHelper
import io.realm.RealmResults

class FlickrPhotosViewModel : ViewModel() {

    val photos: RealmResults<RealmFlickrPhoto>? by lazy {
        loadPhotos()
    }

    private fun loadPhotos() = RealmHelper.queryMyPathPhotos()
}
