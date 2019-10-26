package com.komoot.test.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.komoot.test.R
import com.komoot.test.realm.RealmFlickrPhoto
import com.komoot.test.realm.RealmHelper
import com.komoot.test.ui.adapter.FlickrPhotosRealmAdapter
import com.komoot.test.ui.adapter.RealmAdapterListener
import com.komoot.test.util.LocationTrackerUtil
import com.komoot.test.util.SharedPreferenceHelper.userHasStartedTracking
import io.reactivex.android.schedulers.AndroidSchedulers
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(), RealmAdapterListener {

    private var photos: RealmResults<RealmFlickrPhoto>? = null

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

    @SuppressLint("CheckResult")
    private fun initTrackingSubjectForEmptyView() {
        LocationTrackerUtil.trackingSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { setupEmptyView() }
    }

    override fun onDataInAdapterChanged() {
        shouldScrollRecyclerToTop()
        setupEmptyView()
    }

    private fun shouldScrollRecyclerToTop() {
        photosRecycler?.smoothScrollToPosition(FIRST_ITEM)
    }

    private fun startObservingData() {
        photos = RealmHelper.queryMyPathPhotos()
    }

    private fun setupEmptyView() {
        if (photos.isNullOrEmpty()) {
            emptyViewText?.visibility = View.VISIBLE
            emptyViewText?.setText(if (userHasStartedTracking(activity)) R.string.empty_view_tracking else R.string.empty_view_not_tracking)
        } else {
            emptyViewText?.visibility = View.GONE
        }
    }

    companion object {

        private const val FIRST_ITEM = 0

        fun newInstance() = MainFragment()
    }
}