package com.pathphotographer.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.pathphotographer.app.PathTrackerApplication
import com.pathphotographer.app.R
import com.pathphotographer.app.di.component.FragmentComponent
import com.pathphotographer.app.realm.RealmFlickrPhoto
import com.pathphotographer.app.ui.adapter.FlickrPhotosRealmAdapter
import com.pathphotographer.app.ui.adapter.RealmAdapterListener
import com.pathphotographer.app.util.BaseTrackerHelper
import com.pathphotographer.app.util.FlickrPhotoLocationHandler
import com.pathphotographer.app.util.NetworkConnectionUtils
import com.pathphotographer.app.util.NetworkConnectionUtils.isNetworkConnected
import com.pathphotographer.app.viewmodel.FlickrPhotosViewModel
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

class MainFragment : Fragment(),
    RealmAdapterListener,
    UpdatableFragment {

    @Inject
    lateinit var tracker: BaseTrackerHelper

    @Inject
    lateinit var photosViewModel: FlickrPhotosViewModel

    private var component: FragmentComponent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDaggerComponent()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    private fun initDaggerComponent() {
        component = PathTrackerApplication.initFragmentComponent(this)
        component?.inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        component = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val manager = LinearLayoutManager(context)
        photosRecycler.layoutManager = manager
        photosRecycler.adapter = FlickrPhotosRealmAdapter(photosViewModel.photos, this)
        setupEmptyView()
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

    override fun updateFragmentUI() {
        setupEmptyView()
        checkPendingElementsToFetch()
    }

    private fun checkPendingElementsToFetch() {
        photosViewModel.photos?.forEach { checkIfShouldFetch(it) }
    }

    private fun checkIfShouldFetch(photo: RealmFlickrPhoto) {
        if (photo.url.isNullOrEmpty() && isNetworkConnected(context)) {
            FlickrPhotoLocationHandler().executeFetchPhotoRequest(
                photo.latitude.toFloat(),
                photo.longitude.toFloat(),
                context,
                photo.id
            )
        }
    }

    private fun setupEmptyView() {
        if (photosViewModel.photos.isNullOrEmpty()) {
            emptyViewText?.visibility = View.VISIBLE
            emptyViewText?.setText(if (tracker.isStartedOrStartingTracking) R.string.empty_view_tracking else R.string.empty_view_not_tracking)
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