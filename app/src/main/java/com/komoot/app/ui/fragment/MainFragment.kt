package com.komoot.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.komoot.app.R
import com.komoot.app.lifecycle.RxLifecycleObserver
import com.komoot.app.lifecycle.RxUI
import com.komoot.app.realm.RealmFlickrPhoto
import com.komoot.app.realm.RealmHelper
import com.komoot.app.service.LocationService
import com.komoot.app.ui.adapter.FlickrPhotosRealmAdapter
import com.komoot.app.ui.adapter.RealmAdapterListener
import com.komoot.app.util.LocationTrackerUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(),
    RealmAdapterListener,
    RxUI {

    private var photos: RealmResults<RealmFlickrPhoto>? = null
    private var trackingEventSubscription: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(RxLifecycleObserver(this))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val manager = LinearLayoutManager(context)
        initTrackingSubjectForEmptyView()
        startObservingData()
        photosRecycler.layoutManager = manager
        photosRecycler.adapter = FlickrPhotosRealmAdapter(photos, this)
        setupEmptyView()
    }

    private fun initTrackingSubjectForEmptyView() {
        trackingEventSubscription = LocationTrackerUtil.trackingSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { setupEmptyView() }
    }

    override fun disposeSubscriptions() {
        trackingEventSubscription?.dispose()
    }

    override fun onDataInAdapterChanged() {
        setupEmptyView()
    }

    override fun onNotifyItemRangeInserted(startIndex: Int, length: Int) {
        super.onNotifyItemRangeInserted(startIndex, length)
        shouldScrollRecyclerToTop()
    }

    private fun shouldScrollRecyclerToTop() {
        /* Scroll only if to top and a new item appeared */
        photosRecycler?.post {
            val firstVisibleItemPosition =
                (photosRecycler?.layoutManager as? LinearLayoutManager)?.findFirstCompletelyVisibleItemPosition()

            if (firstVisibleItemPosition == SECOND_ITEM) {
                photosRecycler?.smoothScrollToPosition(FIRST_ITEM)
            }
        }
    }

    private fun startObservingData() {
        photos = RealmHelper.queryMyPathPhotos()
    }

    private fun setupEmptyView() {
        if (photos.isNullOrEmpty()) {
            emptyViewText?.visibility = View.VISIBLE
            emptyViewText?.setText(if (LocationService.isStartedOrStarting) R.string.empty_view_tracking else R.string.empty_view_not_tracking)
        } else {
            emptyViewText?.visibility = View.GONE
        }
    }

    companion object {

        private const val FIRST_ITEM = 0
        private const val SECOND_ITEM = 1

        fun newInstance() = MainFragment()
    }
}