package ru.vladik.playlists.adapters

import android.content.Context
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ru.vladik.playlists.R
import ru.vladik.playlists.dataclasses.musicservises.MusicService
import ru.vladik.playlists.utils.AppServices

class PlatformSpinnerAdapter(private val context: Context) : BaseAdapter() {
    private val platformList = AppServices.getServicesList()

    override fun getCount(): Int = platformList.size
    override fun getItem(position: Int): MusicService = platformList[position]

    override fun getItemId(position: Int) = platformList[position].id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView
            ?: LayoutInflater
                .from(context).inflate(R.layout.platform_spiner_item, parent, false)

        val imageView: ImageView = view.findViewById(R.id.element_image)
        val textView: TextView = view.findViewById(R.id.element_title)

        val item = getItem(position)

        imageView.setImageResource(item.imageRes)
        textView.text = item.name

        return view
    }

}