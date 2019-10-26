package com.komoot.test.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.komoot.test.R
import com.komoot.test.ui.fragment.MainFragment
import com.komoot.test.ui.showSnackbar
import com.komoot.test.ui.switchFragment
import com.komoot.test.util.LocationTrackerUtil
import com.komoot.test.util.LocationTrackerUtil.getLocationPermissions
import com.komoot.test.util.PermissionsUtils
import com.komoot.test.util.PermissionsUtils.allPermissionsGranted
import com.komoot.test.util.PermissionsUtils.getGoToSettingsIntent


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null)
            switchFragment(
                MainFragment.newInstance(),
                R.id.fragment_container,
                false
            )
        requestPermissions()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_REQUEST_CODE) {

            /* User interaction cancelled */
            if (grantResults.isEmpty()) {
                requestPermissions()

                /* Granted */
            } else if (allPermissionsGranted(this, *getLocationPermissions())) {
                startTracking()

                /* Denied, go to settings */
            } else {
                showSnackbar(
                    getString(R.string.permission_denied),
                    R.color.red,
                    getString(R.string.go_to_settings),
                    View.OnClickListener { startActivity(getGoToSettingsIntent()) })
            }
        }
    }

    private fun startTracking() {
        LocationTrackerUtil.startTracking(this)
    }

    private fun requestPermissions() {
        if (PermissionsUtils.shouldRequestPermissions(
                this, *getLocationPermissions()
            )
        ) {
            showSnackbar(
                getString(R.string.ask_location_permission),
                R.color.colorPrimary,
                getString(android.R.string.ok),
                View.OnClickListener { requestLocationPermissions() })

        } else {
            requestLocationPermissions()
        }
    }

    private fun requestLocationPermissions() {
        PermissionsUtils.requestPermissions(this, LOCATION_REQUEST_CODE, *getLocationPermissions())
    }


    companion object {
        private const val LOCATION_REQUEST_CODE = 1234
    }
}
