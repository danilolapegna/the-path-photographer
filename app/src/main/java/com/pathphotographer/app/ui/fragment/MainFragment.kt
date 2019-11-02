package com.pathphotographer.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pathphotographer.app.R
import com.pathphotographer.app.di.component.DaggerTrackerUIComponent
import com.pathphotographer.app.di.module.TrackerContextModule
import com.pathphotographer.app.ui.adapter.FlickrPhotosRealmAdapter
import com.pathphotographer.app.ui.adapter.RealmAdapterListener
import com.pathphotographer.app.util.BaseTrackerHelper
import com.pathphotographer.app.viewmodel.FlickrPhotosViewModel
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

class MainFragment : Fragment(),
    RealmAdapterListener,
    UpdatableFragment {

    @Inject
    lateinit var tracker: BaseTrackerHelper

    private lateinit var photosViewModel: FlickrPhotosViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDaggerComponent()
        photosViewModel = ViewModelProvider(this).get(FlickrPhotosViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    private fun initDaggerComponent() {
        DaggerTrackerUIComponent
            .builder()
            .trackerContextModule(TrackerContextModule())
            .build()
            .inject(this)
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