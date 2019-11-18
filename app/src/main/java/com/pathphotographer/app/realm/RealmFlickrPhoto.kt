package com.pathphotographer.app.realm

import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.*

@RealmClass
open class RealmFlickrPhoto : RealmModel {

    @PrimaryKey
    var id: String? = null

    var url: String? = null

    var photoId: String? = null

    var latitude: Double = 0.0

    var longitude: Double = 0.0

    var fetchedAt: Date? = null
}