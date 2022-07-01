package ru.vladik.playlists.adapters.recycler_view

import android.app.ActionBar
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import ru.vladik.playlists.R
import ru.vladik.playlists.utils.ColorUtils

class EmptyAdapter() : RecyclerView.Adapter<EmptyAdapter.ViewHolder>() {

    data class ViewHolder(var itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(View(parent.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return 0
    }
}
