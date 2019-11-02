package com.pathphotographer.app.util

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationUtils {

    fun updateNotificationText(
        newText: String,
        id: Int,
        builder: NotificationCompat.Builder,
        context: Context
    ) {
        val notificationManager = NotificationManagerCompat.from(context)
        builder.setContentText(newText)
        notificationManager.notify(id, builder.build())
    }
}