package ru.vladik.playlists.adapters.recycler_view

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import ru.vladik.playlists.utils.Extensions.minusOneIfNull
import java.lang.IndexOutOfBoundsException

abstract class HeaderAdapter<T : RecyclerView.ViewHolder>(headerView: View)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var recyclerView: RecyclerView? = null

    var headerView: View = headerView
        set(value) {
            notifyItemChanged(HEADER_POSITION)
            field = value
        }

    companion object {
        const val TYPE_HEADER = -1
        const val HEADER_POSITION = 0
    }

    @CallSuper override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }

    abstract val itemCountWithoutHeader: Int
    abstract fun getViewHolder(parent: ViewGroup, viewType: Int): T
    abstract fun onBindItemViewHolder(holder: T, position: Int)

    open fun getItemsItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    final override fun getItemCount(): Int = itemCountWithoutHeader + 1

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) HeaderHolder(headerView) else getViewHolder(parent, viewType)
    }

    final override fun getItemViewType(position: Int): Int {
        return if (position == HEADER_POSITION) TYPE_HEADER else getItemsItemViewType(position)
    }

    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position != HEADER_POSITION) onBindItemViewHolder(holder as T, position-1)
    }

    class HeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}