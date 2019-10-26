package com.komoot.test.realm

import com.komoot.test.PathTrackerApplication
import com.komoot.test.model.FlickrPhoto
import com.komoot.test.realm.FlickrPhotoTransformer.transformApiItem
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.util.*

object RealmHelper {

    private const val FETCHED_AT_FIELD = "fetchedAt"

    private val applicationContext
        get() = PathTrackerApplication.instance

    fun persistPhoto(requestDate: Date, photo: FlickrPhoto) {
        val realmObject = transformApiItem(requestDate, photo, applicationContext)
        getRealm().executeTransaction {
            it.insertOrUpdate(realmObject)
        }
    }

    fun queryMyPathPhotos(): RealmResults<RealmFlickrPhoto>? {
        return getRealm().where(RealmFlickrPhoto::class.java)
            .sort(FETCHED_AT_FIELD, Sort.DESCENDING)
            .findAllAsync()
    }

    fun clearAllPhotos() {
        getRealm().executeTransaction { realm -> realm.deleteAll() }
    }

    private fun getRealm() = Realm.getDefaultInstance()
}