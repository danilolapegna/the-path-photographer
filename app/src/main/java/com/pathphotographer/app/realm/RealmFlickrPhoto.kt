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

    var fetchedAt: Date? = null
}