package com.komoot.app.client

import android.net.Uri
import com.komoot.app.api.BaseFlickrApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

open class BaseFlickrApiClient {

    private val okHttpClient by lazy { buildOkHttpClient() }

    private val apiAdapter by lazy { buildApiAdapter() }

    private val flickrBaseUrl by lazy {
        Uri.Builder()
            .scheme(SECURE_PROTOCOL)
            .authority(FLICKR_AUTHORITY)
            .appendPath(SERVICES_PATH)
            .build()
            .toString() + "/"
    }

    fun getFlickrService(): BaseFlickrApi = apiAdapter.create(BaseFlickrApi::class.java)

    private fun buildApiAdapter(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(flickrBaseUrl)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(
                SimpleXmlConverterFactory.createNonStrict(
                    Persister(AnnotationStrategy())
                )
            )
            .build()
    }

    private fun buildOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return builder.build()
    }

    companion object {

        private const val SECURE_PROTOCOL = "https"
        private const val FLICKR_AUTHORITY = "www.flickr.com"
        private const val SERVICES_PATH = "services"

    }
}
