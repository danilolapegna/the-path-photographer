package com.komoot.test.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.komoot.test.model.FlickrPhotoResponse
import com.komoot.test.ui.activity.MainActivity
import com.komoot.test.util.DistanceUtils.atLeastAHundredMetersBetweenLocations
import com.komoot.test.util.LocationTrackerUtil.getLastMilestone
import com.komoot.test.util.LocationTrackerUtil.hasMilestone
import com.komoot.test.util.LocationTrackerUtil.setNewMilestone
import com.komoot.test.util.ReactiveNetworkUtils
import com.komoot.test.util.RequestListener
import com.komoot.test.viewmodel.FlickrPhotoRepository
import java.util.concurrent.TimeUnit

class FlickrPhotoService : Service(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    private var googleApiClient: GoogleApiClient? = null
    private var wakeLock: PowerManager.WakeLock? = null

    private val locationCallback: LocationCallback by lazy { buildLocationCallback() }

    private val fusedLocationApiClient by lazy {
        LocationServices.getFusedLocationProviderClient(
            this
        )
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
    }

    /*
     * Phase 1: Connect Client, start notification and getting the updates
     */
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingNotificationIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 0
        )

        builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText(NOTIFICATION_TEXT)
            .setOngoing(true)
            .setContentIntent(pendingNotificationIntent)

        val notification = builder.build()
        startForeground(NOTIFICATION_ID, notification)
        googleApiClient?.connect()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        googleApiClient?.disconnect()
        if (wakeLock?.isHeld == true) {
            wakeLock?.release();
        }
        fusedLocationApiClient.removeLocationUpdates(locationCallback)
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

        override fun onLocationAvailability(p0: LocationAvailability?) {
            super.onLocationAvailability(p0)
        }
    }

    private fun handleLastLocation(location: Location) {
        if (!hasMilestone(this)) {
            setNewMilestone(
                this,
                Pair(location.latitude.toFloat(), location.longitude.toFloat())
            )
        } else {
            val lastMilestone = getLastMilestone(this)
            if (atLeastAHundredMetersBetweenLocations(lastMilestone, location)) {
                val newMilestoneLatitude = location.latitude.toFloat()
                val newMilestoneLongitude = location.longitude.toFloat()
                executeFetchPhotoRequest(newMilestoneLatitude, newMilestoneLongitude)
                setNewMilestone(
                    this,
                    Pair(newMilestoneLatitude, newMilestoneLongitude)
                )
            }
        }
    }

    private fun executeFetchPhotoRequest(
        newMilestoneLatitude: Float,
        newMilestoneLongitude: Float
    ) {
        val request =
            FlickrPhotoRepository.getPhotoByCoordinates(newMilestoneLatitude, newMilestoneLongitude)
        ReactiveNetworkUtils.executeRequest(request, getRequestListener())
    }

    private fun getRequestListener() = object : RequestListener<FlickrPhotoResponse> {
        override fun onSuccess(response: FlickrPhotoResponse) {
            Log.d("Wow!", "It worked")
        }

        override fun onFailure(exception: Throwable) {
            Log.d("Nay!", "It didn't work")
        }

    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    private fun getLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = TimeUnit.SECONDS.toMillis(0)
        locationRequest.fastestInterval = TimeUnit.SECONDS.toMillis(0)
        locationRequest.maxWaitTime = UPDATE_INTERVAL_IN_MS
        return locationRequest
    }

    companion object {
        private const val CHANNEL_ID = "location_Tracker_Id"
        private const val CHANNEL_NAME = "Foreground Service Channel"

        private const val NOTIFICATION_TITLE = "Notification Tracker is active"
        private const val NOTIFICATION_TEXT = "Hold on while we register your walk"

        private const val UPDATE_INTERVAL_IN_MS = 2000L

        private const val NOTIFICATION_ID = 1

        private const val TAG = "myapp:flickrPhotoTag"
    }

}