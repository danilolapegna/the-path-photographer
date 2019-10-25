package com.komoot.test.model

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import java.io.Serializable

@Root(name = "photos", strict = false)
class FlickrPhotoResponse {

    @ElementList
    var photos: ArrayList<FlickrPhoto>? = null
}

class FlickrPhoto : Serializable {

    @Attribute(name = "id", required = true)
    var id: String? = null

    @Attribute(name = "owner", required = true)
    var owner: String? = null

    @Attribute(name = "secret", required = true)
    var secret: String? = null

    @Attribute(name = "server", required = true)
    var server: String? = null

    @Attribute(name = "title", required = true)
    var title: String? = null

    @Attribute(name = "ispublic", required = false)
    var ispublic: String? = null

    @Attribute(name = "isfriend", required = false)
    var isfriend: String? = null
}