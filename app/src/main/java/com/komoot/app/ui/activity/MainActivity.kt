package com.komoot.app.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.komoot.app.R
import com.komoot.app.lifecycle.RxLifecycleObserver
import com.komoot.app.lifecycle.RxUI
import com.komoot.app.realm.RealmHelper
import com.komoot.app.service.LocationService
import com.komoot.app.ui.displayToast
import com.komoot.app.ui.fragment.MainFragment
import com.komoot.app.ui.showSnackbar
import com.komoot.app.ui.switchFragment
import com.komoot.app.util.BaseTrackerHelper
import com.komoot.app.util.LocationTrackerHelper
import com.komoot.app.util.PermissionsUtils
import com.komoot.app.util.PermissionsUtils.allPermissionsGranted
import com.komoot.app.util.PermissionsUtils.getGoToSettingsIntent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), RxUI {

    private var trackingEventSubscription: Disposable? = null

    private val tracker : BaseTrackerHelper by lazy { LocationTrackerHelper.instance }

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
        bindViewToTrackingState()
        lifecycle.addObserver(RxLifecycleObserver(this))
    }

    private fun bindViewToTrackingState() {
        trackingEventSubscription = tracker.trackingObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { tracking ->
                setupFloatingActionButton()
                displayToast(if (tracking) R.string.started_tracking else R.string.stopped_tracking)
            }
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

    override fun disposeSubscriptions() {
        trackingEventSubscription?.dispose()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_reset -> {
                RealmHelper.clearAllPhotos()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupFloatingActionButtonDrawable() {
        if (userHasStartedTracking()) {
            playStopFab?.setImageResource(R.drawable.ic_stop)
        } else {
            playStopFab?.setImageResource(R.drawable.ic_play)
        }
    }

    private fun userHasStartedTracking(): Boolean = LocationService.isStartedOrStarting

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_REQUEST_CODE) {

            /* User interaction cancelled */
            if (grantResults.isEmpty()) {
                requestLocationPermissions()

                /* Granted */
            } else if (allPermissionsGranted(this, *tracker.getLocationPermissions())) {
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
        tracker.startTracking(this)
    }

    private fun stopTracking() {
        tracker.stopTracking(this)
    }

    private fun requestLocationPermissions() {
        PermissionsUtils.requestPermissions(this, LOCATION_REQUEST_CODE, *tracker.getLocationPermissions())
    }

    companion object {
        private const val LOCATION_REQUEST_CODE = 1234
    }
}
