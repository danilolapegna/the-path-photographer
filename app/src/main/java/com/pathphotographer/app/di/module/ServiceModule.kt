package com.pathphotographer.app.di.module

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.pathphotographer.app.R
import com.pathphotographer.app.di.scope.ServiceScope
import com.pathphotographer.app.service.LocationService
import com.pathphotographer.app.ui.activity.MainActivity
import com.pathphotographer.app.util.BaseTrackerHelper
import com.pathphotographer.app.util.LocationTrackerHelper
import dagger.Module
import dagger.Provides
import java.util.concurrent.TimeUnit


@Module
class ServiceModule(val locationService: LocationService) {

    @Provides
    @ServiceScope
    internal fun provideTracker(): BaseTrackerHelper = LocationTrackerHelper()

    @Provides
    @ServiceScope
    internal fun provideGoogleApiClient(): GoogleApiClient =
        GoogleApiClient.Builder(locationService)
            .addConnectionCallbacks(locationService)
            .addOnConnectionFailedListener(locationService)
            .addApi(LocationServices.API)
            .build()

    @Provides
    @ServiceScope
    internal fun provideLocationCallback(): LocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)
            p0?.lastLocation?.let { lastLocation -> locationService.handleLastLocation(lastLocation) }

        }
    }

    @Provides
    @ServiceScope
    internal fun provideFusedLocationProvider() =
        LocationServices.getFusedLocationProviderClient(
            locationService
        )

    @Provides
    @ServiceScope
    internal fun provideNotificationBuilder(): NotificationCompat.Builder {
        val notificationIntent = Intent(locationService, MainActivity::class.java)
        val pendingNotificationIntent = PendingIntent.getActivity(
            locationService, 0, notificationIntent, 0
        )

        return NotificationCompat.Builder(locationService, LocationService.CHANNEL_ID)
            .setContentTitle(locationService.getString(R.string.notification_title))
            .setContentText(locationService.getString(R.string.notification_text))
            .setSmallIcon(R.drawable.ic_camera_white)
            .setOngoing(true)
            .setContentIntent(pendingNotificationIntent)
    }

    @Provides
    @ServiceScope
    internal fun provideLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = TimeUnit.SECONDS.toMillis(LocationService.INTERVAL_MIN)
            fastestInterval = TimeUnit.SECONDS.toMillis(LocationService.INTERVAL_MIN)
            maxWaitTime = LocationService.MAX_WAIT_TIME_IN_MS
        }
    }

    @Provides
    @ServiceScope
    internal fun provideWakeLock(): PowerManager.WakeLock {
        val powerManager = (locationService.getSystemService(Context.POWER_SERVICE) as? PowerManager)
        return powerManager?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LocationService.TAG)!!
    }
}