package com.pathphotographer.app.realm

import android.content.Context
import com.pathphotographer.app.PathTrackerApplication
import com.pathphotographer.app.model.FlickrPhoto
import com.pathphotographer.app.realm.FlickrPhotoTransformer.transformApiItem
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.util.*
import javax.inject.Inject

class RealmHelper {

    @Inject
    lateinit var applicationContext: Context

    init {
        PathTrackerApplication
            .applicationComponent
            .inject(this)
    }

    fun persistPhoto(requestDate: Date, photo: FlickrPhoto) {
        val realmObject = transformApiItem(requestDate, photo, applicationContext)
        getRealm().executeTransaction {
            it.insertOrUpdate(realmObject)
        }
    }

    fun queryMyPathPhotos(): RealmResults<RealmFlickrPhoto>? =
        getRealm().where(RealmFlickrPhoto::class.java)
            .sort(FETCHED_AT_FIELD, Sort.DESCENDING)
            .findAllAsync()

    fun clearAllPhotos() {
        getRealm().executeTransaction { realm -> realm.deleteAll() }
    }

    private fun getRealm() = Realm.getDefaultInstance()

    companion object {

        private const val FETCHED_AT_FIELD = "fetchedAt"

        val instance = RealmHelper()
    }
}