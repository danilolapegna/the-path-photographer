package com.komoot.test.util

import android.net.Uri
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder

object DraweeImageLoader {

    fun loadImage(url: String, draweeView: SimpleDraweeView) {
        val draweeBuilder = GenericDraweeHierarchyBuilder(draweeView.resources)

        val requestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
            .setLocalThumbnailPreviewsEnabled(true)
            .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
            .setCacheChoice(ImageRequest.CacheChoice.DEFAULT)
            .setProgressiveRenderingEnabled(false)

        val controller = Fresco
            .newDraweeControllerBuilder()
            .setOldController(draweeView.controller)
            .setImageRequest(requestBuilder?.build())
        draweeView.hierarchy = draweeBuilder.build()
        draweeView.controller = controller.build()
    }
}