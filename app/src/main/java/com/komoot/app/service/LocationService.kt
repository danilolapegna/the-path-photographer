package com.komoot.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.*
import androidx.core.app.NotificationCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.komoot.app.R
import com.komoot.app.ui.activity.MainActivity
import com.komoot.app.util.BaseTrackerHelper
import com.komoot.app.util.LocationTrackerHelper
import com.komoot.app.util.NotificationUtils.updateNotificationText
import java.util.concurrent.TimeUnit

class LocationService : Service(),
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    private var googleApiClient: GoogleApiClient? = null
    private var wakeLock: PowerManager.WakeLock? = null

    private val tracker : BaseTrackerHelper by lazy { LocationTrackerHelper.instance }

    private val locationCallback: LocationCallback by lazy { buildLocationCallback() }

    private val fusedLocationApiClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private lateinit var builder: NotificationCompat.Builder

    override fun onBind(p0: Intent?): IBinder? = null

    /*
     * Phase 1: Init Google Api Client
     */
    override fun onCreate() {
        super.onCreate()
        googleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()

        val powerManager = (getSystemService(Context.POWER_SERVICE) as? PowerManager)
        wakeLock = powerManager?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingNotificationIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 0
        )

        builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(R.drawable.ic_camera_white)
            .setOngoing(true)
            .setContentIntent(pendingNotificationIntent)
    }

    /*
     * Phase 2: Connect Client, start notification and getting the updates
     */
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        isStartedOrStarting = true
        createNotificationChannel()
        val notification = builder.build()
        startForeground(NOTIFICATION_ID, notification)
        googleApiClient?.connect()
        tracker.startTracking(this, false)
        return START_STICKY
    }

    override fun onDestroy() {
        isStartedOrStarting = false
        stopForeground(true)
        googleApiClient?.disconnect()
        if (wakeLock?.isHeld == true) {
            wakeLock?.release();
        }
        fusedLocationApiClient.removeLocationUpdates(locationCallback)
        tracker.stopTracking(this, false)
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val locationTrackerChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                locationTrackerChannel
            )
        }
    }

    override fun onConnected(p0: Bundle?) {
        fusedLocationApiClient.requestLocationUpdates(
            getLocationRequest(),
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun buildLocationCallback(): LocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)
            p0?.lastLocation?.let { lastLocation -> handleLastLocation(lastLocation) }
        }
    }

    private fun handleLastLocation(location: Location) {
        val baseText = getString(R.string.notification_text_location)
        updateNotificationText(
            String.format(baseText, location.latitude, location.longitude),
            NOTIFICATION_ID,
            builder,
            this
        )
        locationHandlers.forEach { it.handleLocation(location, this) }
    }

    override fun onConnectionSuspended(p0: Int) {}

    override fun onConnectionFailed(p0: ConnectionResult) {}

    private fun getLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = TimeUnit.SECONDS.toMillis(INTERVAL_MIN)
            fastestInterval = TimeUnit.SECONDS.toMillis(INTERVAL_MIN)
            maxWaitTime = MAX_WAIT_TIME_IN_MS
        }
    }

    companion object {

        var isStartedOrStarting = false

        private val locationHandlers: ArrayList<LocationServiceHandler> = ArrayList()

        private const val CHANNEL_ID = "location_Tracker_Id"
        private const val CHANNEL_NAME = "Foreground Service Channel"

        private const val INTERVAL_MIN = 0L

        private const val MAX_WAIT_TIME_IN_MS = 2000L

        private const val NOTIFICATION_ID = 1

        private const val TAG = "myapp:flickrPhotoTag"

        fun addHandler(locationServiceHandler: LocationServiceHandler) {
            locationHandlers.add(locationServiceHandler)
        }

        fun removeHandler(locationServiceHandler: LocationServiceHandler) {
            locationHandlers.remove(locationServiceHandler)
        }

        fun clearAllHandlers() {
            locationHandlers.clear()
        }
    }
}

interface LocationServiceHandler {

    fun handleLocation(location: Location, context: Context)
}