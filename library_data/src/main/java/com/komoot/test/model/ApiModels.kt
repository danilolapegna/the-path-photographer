package com.komoot.test.model

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import java.io.Serializable

@Root(name = "photos", strict = false)
class FlickrPhotoResponse {

    @set:ElementList(name = "photos")
    @get:ElementList(name = "photos")
    var photos: ArrayList<FlickrPhoto>? = null
}

class FlickrPhoto : Serializable {

    @set:Attribute(name = "id", required = true)
    @get:Attribute(name = "id", required = true)
    var id: String? = null

    @set:Attribute(name = "owner", required = true)
    @get:Attribute(name = "owner", required = true)
    var owner: String? = null

    @set:Attribute(name = "secret", required = true)
    @get:Attribute(name = "secret", required = true)
    var secret: String? = null

    @set:Attribute(name = "server", required = true)
    @get:Attribute(name = "server", required = true)
    var server: String? = null

    @set:Attribute(name = "farm", required = true)
    @get:Attribute(name = "farm", required = true)
    var farm: String? = null

    @set:Attribute(name = "title", required = true)
    @get:Attribute(name = "title", required = true)
    var title: String? = null

    @set:Attribute(name = "ispublic", required = false)
    @get:Attribute(name = "ispublic", required = false)
    var ispublic: String? = null

    @set:Attribute(name = "isfriend", required = false)
    @get:Attribute(name = "isfriend", required = false)
    var isfriend: String? = null
}