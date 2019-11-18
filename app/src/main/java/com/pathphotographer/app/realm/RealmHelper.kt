package com.pathphotographer.app.realm

import android.content.Context
import com.pathphotographer.app.PathTrackerApplication
import com.pathphotographer.app.model.FlickrPhoto
import com.pathphotographer.app.realm.FlickrPhotoTransformer.generateRealmItem
import com.pathphotographer.app.util.ContextConsumer
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.util.*
import javax.inject.Inject

class RealmHelper : ContextConsumer {

    @Inject
    override lateinit var applicationContext: Context

    init {
        injectDagger()
    }

    private fun injectDagger() {
        PathTrackerApplication
            .applicationComponent
            .inject(this)
    }

    /*
     * photo: if it's a new item from api
     * previousItemId: if it's an already existing item (eg. no network) from api
     * No photo, no previousItemId: if it's a new no network item
     */
    fun persistPhotoItem(
        requestDate: Date,
        lat: Double,
        lon: Double,
        photo: FlickrPhoto? = null,
        previousItemId: String? = null
    ) {
        val realmObject =
            generateRealmItem(requestDate, applicationContext, lat, lon, photo, previousItemId)
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

        val instance by lazy { RealmHelper() }
    }
}