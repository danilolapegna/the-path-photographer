package com.komoot.test.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.komoot.test.R
import com.komoot.test.realm.RealmHelper
import com.komoot.test.service.FlickrPhotoService
import com.komoot.test.ui.fragment.MainFragment
import com.komoot.test.ui.showSnackbar
import com.komoot.test.ui.switchFragment
import com.komoot.test.util.LocationTrackerUtil
import com.komoot.test.util.LocationTrackerUtil.getLocationPermissions
import com.komoot.test.util.PermissionsUtils
import com.komoot.test.util.PermissionsUtils.allPermissionsGranted
import com.komoot.test.util.PermissionsUtils.getGoToSettingsIntent
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_reset -> {
                RealmHelper.clearAllPhotos()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun setupFloatingActionButtonDrawable() {
        if (userHasStartedTracking()) {
            playStopFab?.setImageResource(R.drawable.ic_stop)
        } else {
            playStopFab?.setImageResource(R.drawable.ic_play)
        }
    }

    private fun userHasStartedTracking(): Boolean = FlickrPhotoService.isStartedOrStarting

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
        Toast.makeText(this, R.string.started_tracking, Toast.LENGTH_SHORT).show()
        setupFloatingActionButtonDrawable()
    }

    private fun stopTracking() {
        LocationTrackerUtil.stopTracking(this)
        Toast.makeText(this, R.string.stopped_tracking, Toast.LENGTH_SHORT).show()
        setupFloatingActionButtonDrawable()
    }

    private fun requestLocationPermissions() {
        PermissionsUtils.requestPermissions(this, LOCATION_REQUEST_CODE, *getLocationPermissions())
    }


    companion object {
        private const val LOCATION_REQUEST_CODE = 1234
    }
}
