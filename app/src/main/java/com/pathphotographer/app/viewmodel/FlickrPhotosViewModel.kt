package com.pathphotographer.app.viewmodel

import androidx.lifecycle.ViewModel
import com.pathphotographer.app.realm.RealmFlickrPhoto
import com.pathphotographer.app.realm.RealmHelper
import io.realm.RealmResults

class FlickrPhotosViewModel : ViewModel() {

    val photos: RealmResults<RealmFlickrPhoto>? by lazy {
        loadPhotos()
    }

    private fun loadPhotos() = RealmHelper.instance.queryMyPathPhotos()
}
