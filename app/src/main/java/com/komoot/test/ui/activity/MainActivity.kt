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
import com.komoot.test.util.SharedPreferenceHelper
import kotlinx.android.synthetic.main.activity_main.*


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
        setupFloatingActionButton()
    }

    private fun setupFloatingActionButton() {
        setupFloatingActionButtonClick()
        setupFloatingActionButtonDrawable()
    }

    private fun setupFloatingActionButtonClick() {
        playStopFab?.setOnClickListener {
            if (userHasStartedTracking()) stopTracking() else requestLocationPermissions()
        }
    }

    private fun setupFloatingActionButtonDrawable() {
        if (userHasStartedTracking()) {
            playStopFab?.setImageResource(R.drawable.ic_stop)
        } else {
            playStopFab?.setImageResource(R.drawable.ic_play)
        }
    }

    private fun userHasStartedTracking(): Boolean =
        SharedPreferenceHelper.userHasStartedTracking(this)

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_REQUEST_CODE) {

            /* User interaction cancelled */
            if (grantResults.isEmpty()) {
                requestLocationPermissions()

                /* Granted */
            } else if (allPermissionsGranted(this, *getLocationPermissions())) {
                startTracking()

                /* Denied, propose to go to settings */
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
        setupFloatingActionButtonDrawable()
    }

    private fun stopTracking() {
        LocationTrackerUtil.stopTracking(this)
        setupFloatingActionButtonDrawable()
    }

    private fun requestLocationPermissions() {
        PermissionsUtils.requestPermissions(this, LOCATION_REQUEST_CODE, *getLocationPermissions())
    }


    companion object {
        private const val LOCATION_REQUEST_CODE = 1234
    }
}
