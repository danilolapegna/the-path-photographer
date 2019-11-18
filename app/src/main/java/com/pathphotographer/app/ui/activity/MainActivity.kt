package com.pathphotographer.app.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.pathphotographer.app.PathTrackerApplication
import com.pathphotographer.app.R
import com.pathphotographer.app.di.component.ActivityComponent
import com.pathphotographer.app.lifecycle.NetworkChangeCallbackUI
import com.pathphotographer.app.lifecycle.NetworkChangeLifecycleObserver
import com.pathphotographer.app.lifecycle.RxLifecycleObserver
import com.pathphotographer.app.lifecycle.RxUI
import com.pathphotographer.app.realm.RealmHelper
import com.pathphotographer.app.ui.displayToast
import com.pathphotographer.app.ui.fragment.MainFragment
import com.pathphotographer.app.ui.fragment.UpdatableFragment
import com.pathphotographer.app.ui.getFragmentInContainer
import com.pathphotographer.app.ui.showSnackbar
import com.pathphotographer.app.ui.switchFragment
import com.pathphotographer.app.util.BaseConnectionStateMonitor
import com.pathphotographer.app.util.BaseTrackerHelper
import com.pathphotographer.app.util.ConnectionState
import com.pathphotographer.app.util.PermissionsUtils
import com.pathphotographer.app.util.PermissionsUtils.allPermissionsGranted
import com.pathphotographer.app.util.PermissionsUtils.getGoToSettingsIntent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : AppCompatActivity(), RxUI, NetworkChangeCallbackUI {

    @Inject
    lateinit var tracker: BaseTrackerHelper

    @Inject
    lateinit var networkMonitor: BaseConnectionStateMonitor

    private var component: ActivityComponent? = null
    private var trackingEventSubscription: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null)
            switchFragment(
                MainFragment.newInstance(),
                R.id.fragment_container,
                false
            )
        initDaggerComponent()
        setupFloatingActionButton()
        bindViewToTrackingState()
        lifecycle.addObserver(RxLifecycleObserver(this))
        lifecycle.addObserver(NetworkChangeLifecycleObserver(this))
        networkMonitor.enable(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        networkMonitor.disable(this)
        component = null
    }

    private fun initDaggerComponent() {
        component = PathTrackerApplication.initActivityComponent()
        component?.inject(this)
    }

    private fun bindViewToTrackingState() {
        trackingEventSubscription = tracker.trackingObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { tracking ->
                setupFloatingActionButton()
                setupFragmentUI()
                displayToast(if (tracking) R.string.started_tracking else R.string.stopped_tracking)
            }
    }

    private fun setupFragmentUI() {
        (getFragmentInContainer() as? UpdatableFragment)?.updateFragmentUI()
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
                RealmHelper.instance.clearAllPhotos()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onConnectionChange(connectionState: ConnectionState) {
        runOnUiThread {
            (getFragmentInContainer() as? UpdatableFragment)?.updateFragmentUI()
        }
    }

    private fun setupFloatingActionButtonDrawable() {
        if (userHasStartedTracking()) {
            playStopFab?.setImageResource(R.drawable.ic_stop)
        } else {
            playStopFab?.setImageResource(R.drawable.ic_play)
        }
    }

    private fun userHasStartedTracking(): Boolean = tracker.isStartedOrStartingTracking

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
        PermissionsUtils.requestPermissions(
            this,
            LOCATION_REQUEST_CODE,
            *tracker.getLocationPermissions()
        )
    }

    companion object {
        private const val LOCATION_REQUEST_CODE = 1234
    }
}
