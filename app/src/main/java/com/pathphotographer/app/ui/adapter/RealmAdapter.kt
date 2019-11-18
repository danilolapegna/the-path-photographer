package com.pathphotographer.app.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedCollectionChangeSet
import io.realm.OrderedRealmCollectionChangeListener
import io.realm.RealmModel
import io.realm.RealmResults


/*
 * Logic partially inferred from:
 * https://github.com/realm/realm-android-adapters/blob/master/adapters/src/main/java/io/realm/RealmRecyclerViewAdapter.java
 */

abstract class RealmAdapter<T : RecyclerView.ViewHolder, V : RealmModel>(
    val data: RealmResults<V>?,
    val UIListener: RealmAdapterListener?
) :
    RecyclerView.Adapter<T>() {

    private var dataChangeListener: OrderedRealmCollectionChangeListener<RealmResults<V>>

    init {
        dataChangeListener = createDataChangeListener()
    }

    private fun createDataChangeListener(): OrderedRealmCollectionChangeListener<RealmResults<V>> {
        return object : OrderedRealmCollectionChangeListener<RealmResults<V>> {
            override fun onChange(t: RealmResults<V>, changeSet: OrderedCollectionChangeSet) {
                if (changeSet.state == OrderedCollectionChangeSet.State.INITIAL) {
                    notifyDataSetChanged()
                    UIListener?.onNotifyDataSetChanged()
                    return
                }
                val deletions = changeSet.deletionRanges
                for (i in deletions.indices.reversed()) {
                    val range = deletions[i]
                    notifyItemRangeRemoved(range.startIndex + dataOffset(), range.length)
                    UIListener?.onNotifyItemRangeRemoved(
                        range.startIndex + dataOffset(),
                        range.length
                    )
                }

                val insertions = changeSet.insertionRanges
                for (range in insertions) {
                    notifyItemRangeInserted(range.startIndex + dataOffset(), range.length)
                    UIListener?.onNotifyItemRangeInserted(
                        range.startIndex + dataOffset(),
                        range.length
                    )
                }

                val modifications = changeSet.changeRanges
                for (range in modifications) {
                    notifyItemRangeChanged(range.startIndex + dataOffset(), range.length)
                    UIListener?.onNotifyItemRangeChanged(
                        range.startIndex + dataOffset(),
                        range.length
                    )
                }
            }
        }
    }

    /*
     * Override if has headers/footers
     */
    open fun dataOffset(): Int = 0

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        if (isDataValid()) {
            addListener(data)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        if (isDataValid()) {
            removeListener(data)
        }
    }

    private fun addListener(data: RealmResults<V>?) {
        data?.addChangeListener(dataChangeListener)
    }

    private fun removeListener(data: RealmResults<V>?) {
        data?.removeChangeListener(dataChangeListener)
    }

    override fun getItemCount(): Int = data?.size ?: 0

    private fun isDataValid(): Boolean = data?.isValid == true

}

interface RealmAdapterListener {

    fun onDataInAdapterChanged()

    fun onNotifyDataSetChanged() {
        onDataInAdapterChanged()
    }

    fun onNotifyItemRangeRemoved(startIndex: Int, length: Int) {
        onDataInAdapterChanged()
    }

    fun onNotifyItemRangeInserted(startIndex: Int, length: Int) {
        onDataInAdapterChanged()
    }

    fun onNotifyItemRangeChanged(startIndex: Int, length: Int) {
        onDataInAdapterChanged()
    }
}
