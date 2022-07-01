package ru.vladik.playlists.utils

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.IdRes
import ru.vladik.playlists.R

object LayoutHelper {

    fun setLoading(parent: ViewGroup, loading: Boolean = true, @IdRes excludeList: List<Int>? = null) {
        if (loading) {
            for (i in 0 until parent.childCount) {
                val view = parent.getChildAt(i)
                if (excludeList == null || !excludeList.contains(view.id)) {
                    view.visibility = View.GONE
                }
            }
            LayoutInflater.from(parent.context)
                .inflate(parent.context.resources.getLayout(R.layout.loading_layout),
                         parent, true)
        } else {
            val contentLoadingProgressBar =
                parent.findViewById<ProgressBar>(R.id.loading_progress_bar)
            if (contentLoadingProgressBar != null) {
                parent.removeView(contentLoadingProgressBar)
            }
            for (i in 0 until parent.childCount) {
                val view = parent.getChildAt(i)
                if (excludeList == null || !excludeList.contains(view.id)) {
                    view.visibility = View.VISIBLE
                }
            }
        }
    }
}