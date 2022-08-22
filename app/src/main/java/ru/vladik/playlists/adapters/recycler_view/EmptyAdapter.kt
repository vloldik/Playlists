package ru.vladik.playlists.adapters.recycler_view

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

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
