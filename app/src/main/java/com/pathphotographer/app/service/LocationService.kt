package com.pathphotographer.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.*
import androidx.core.app.NotificationCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.pathphotographer.app.PathTrackerApplication
import com.pathphotographer.app.R
import com.pathphotographer.app.di.component.ServiceComponent
import com.pathphotographer.app.util.BaseTrackerHelper
import com.pathphotographer.app.util.NotificationUtils.updateNotificationText
import javax.inject.Inject

class LocationService : Service(),
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    @Inject
    lateinit var wakeLock: PowerManager.WakeLock

    @Inject
    lateinit var tracker: BaseTrackerHelper

    @Inject
    lateinit var googleApiClient: GoogleApiClient

    @Inject
    lateinit var locationCallback: LocationCallback

    @Inject
    lateinit var fusedLocationApiClient: FusedLocationProviderClient

    @Inject
    lateinit var builder: NotificationCompat.Builder

    @Inject
    lateinit var locationRequest: LocationRequest

    private var component: ServiceComponent? = null

    override fun onBind(p0: Intent?): IBinder? = null

    /*
     * Phase 1: Init Google Api Client
     */
    override fun onCreate() {
        super.onCreate()
        initDaggerComponent()
    }

    private fun initDaggerComponent() {
        component = PathTrackerApplication.initServiceComponent(this)
        component?.inject(this)
    }

    /*
     * Phase 2: Connect Client, start notification and getting the updates
     */
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        isStartedOrStarting = true
        createNotificationChannel()
        val notification = builder.build()
        startForeground(NOTIFICATION_ID, notification)
        googleApiClient.connect()
        tracker.startTracking(this, false)
        return START_STICKY
    }

    override fun onDestroy() {
        isStartedOrStarting = false
        stopForeground(true)
        googleApiClient.disconnect()
        if (wakeLock.isHeld) wakeLock.release();

        fusedLocationApiClient.removeLocationUpdates(locationCallback)
        tracker.stopTracking(this, false)
        component = null
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val locationTrackerChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(
                locationTrackerChannel
            )
        }
    }

    override fun onConnected(p0: Bundle?) {
        fusedLocationApiClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onConnectionSuspended(p0: Int) {}

    override fun onConnectionFailed(p0: ConnectionResult) {}

    fun handleLastLocation(location: Location) {
        val baseText = getString(R.string.notification_text_location)
        updateNotificationText(
            String.format(baseText, location.latitude, location.longitude),
            NOTIFICATION_ID,
            builder,
            this
        )
        locationHandlers.forEach { it.handleLocation(location, this) }
    }

    companion object {

        internal var isStartedOrStarting = false

        private val locationHandlers: ArrayList<LocationServiceHandler> = ArrayList()

        const val CHANNEL_ID = "location_Tracker_Id"
        const val CHANNEL_NAME = "Foreground Service Channel"
        const val INTERVAL_MIN = 0L
        const val MAX_WAIT_TIME_IN_MS = 2000L

        private const val NOTIFICATION_ID = 1

        const val TAG = "myapp:flickrPhotoTag"

        fun addHandler(locationServiceHandler: LocationServiceHandler) {
            locationHandlers.add(locationServiceHandler)
        }

        fun clearAllHandlers() {
            locationHandlers.clear()
        }
    }
}

interface LocationServiceHandler {

    fun handleLocation(location: Location, context: Context)
}