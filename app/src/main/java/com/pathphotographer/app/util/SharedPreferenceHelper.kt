package com.pathphotographer.app.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager

object SharedPreferenceHelper {

    const val TAG = "PreferenceUtils"

    private const val FLOAT_MILESTONE_LAST_LAT_KEY = "last_milestone_lat"
    private const val FLOAT_MILESTONE_LAST_LON_KEY = "last_milestone_lon"

    private fun getSharedPreferences(context: Context?) =
        PreferenceManager.getDefaultSharedPreferences(context)

   /*
    * Milestone: either the start location or the last time 100 meters
    * were counted
    */
    fun storeLocationMilestone(
        context: Context?,
        latitude: Float,
        longitude: Float
    ) {
        putFloat(context, FLOAT_MILESTONE_LAST_LAT_KEY, latitude)
        putFloat(context, FLOAT_MILESTONE_LAST_LON_KEY, longitude)
    }

    fun getLastLocationMilestone(context: Context?): Pair<Float, Float> {
        val latitude = getFloat(context, FLOAT_MILESTONE_LAST_LAT_KEY, -1f)
        val longitude = getFloat(context, FLOAT_MILESTONE_LAST_LON_KEY, -1f)
        return Pair(latitude, longitude)
    }

    fun hasLastLocationMilestone(context: Context?): Boolean =
        hasAllKeys(context, FLOAT_MILESTONE_LAST_LAT_KEY, FLOAT_MILESTONE_LAST_LON_KEY)

    fun clearLastLocationMilestone(context: Context?) {
        removeKeys(context, FLOAT_MILESTONE_LAST_LAT_KEY, FLOAT_MILESTONE_LAST_LON_KEY)
    }

    private fun getFloat(
        context: Context?,
        key: String,
        defaultValue: Float = -1f
    ): Float {
        return getValue(
            context,
            { (getSharedPreferences(context)).getFloat(key, defaultValue) }, defaultValue
        ) ?: defaultValue
    }

    private fun putFloat(
        context: Context? = null,
        key: String,
        value: Float
    ) {
        store(context) { it.putFloat(key, value) }
    }

    private fun removeKeys(context: Context?, vararg keys: String?) {
        keys.forEach { removeKey(context, it) }
    }

    private fun removeKey(context: Context?, key: String?) {
        val prefs = getSharedPreferences(context)
        if (key != null && prefs.contains(key)) {
            prefs.edit().remove(key).apply()
        }
    }

    private fun hasAllKeys(context: Context?, vararg keys: String): Boolean {
        keys.forEach { if (!hasKey(context, it)) return false }
        return true
    }

    private fun hasKey(context: Context?, key: String): Boolean =
        (getSharedPreferences(context)).contains(key)

    private fun store(
        context: Context?,
        operation: (SharedPreferences.Editor) -> Unit
    ) {
        try {
            val editor = getSharedPreferences(context).edit()
            operation(editor)
            editor.apply()
        } catch (e: Exception) {
            Log.e(TAG, "Can't store value in preferences", e)
        }
    }

    private fun <T> getValue(
        context: Context?,
        operation: (SharedPreferences?) -> T,
        defaultValue: T?
    ): T? {
        try {
            return operation(getSharedPreferences(context))
        } catch (e: Exception) {
            Log.e(TAG, "Error while getting the value", e)
            return null
        }
    }
}