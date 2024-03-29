package com.pathphotographer.app.util

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.pathphotographer.app.BuildConfig

object PermissionsUtils {

    private const val PACKAGE_URI_PART = "package"

    fun getGoToSettingsIntent(): Intent {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts(PACKAGE_URI_PART, BuildConfig.APPLICATION_ID, null)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        return intent
    }

    fun allPermissionsGranted(activity: Activity?, vararg permissions: String): Boolean {
        return permissions.filter { !isPermissionGranted(activity, it) }.isEmpty()
    }

    private fun isPermissionGranted(activity: Activity?, permission: String): Boolean {
        if (activity == null) {
            return false
        }
        return ContextCompat.checkSelfPermission(
            activity,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions(
        activity: FragmentActivity,
        requestCode: Int,
        vararg permissions: String
    ) {
        ActivityCompat.requestPermissions(
            activity,
            permissions,
            requestCode
        )
    }
}