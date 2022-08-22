package ru.vladik.playlists.adapters.recycler_view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.request.RequestOptions
import io.gresse.hugo.vumeterlibrary.VuMeterView
import jp.wasabeef.glide.transformations.CropTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import ru.vladik.playlists.R
import ru.vladik.playlists.dataclasses.tracks.TrackModel
import ru.vladik.playlists.utils.StringUtil

class TracksAdapter(val context: Context) : RecyclerView.Adapter<TracksAdapter.ViewHolder>() {

    private val trackDefaultImage: Drawable = AppCompatResources.getDrawable(context, R.drawable.track)!!
    var currentPlayingId = ""
        set(value) {
            val prevId = currentPlayingId
            field = value

            if (currentPlayingId == prevId) return

            notifyItemChanged(getIndexById(prevId))

            if (currentPlayingId == "") return

            notifyItemChanged(getIndexById(currentPlayingId))
        }

    var isPaused: Boolean = false
        set(value) {
            field = value
            if (currentPlayingId != "") notifyItemChanged(getIndexById(currentPlayingId))
        }

    var eventListener: EventListener? = null

    data class ViewHolder(
        var itemView: View,
        val image: ImageView = itemView.findViewById(R.id.element_image),
        var title: TextView = itemView.findViewById(R.id.element_title),
        var authors: TextView = itemView.findViewById(R.id.element_description),
        var mainLayout: View = itemView.findViewById(R.id.element_main_layout),
        var imageContainer: ViewGroup = itemView.findViewById(R.id.element_image_container),
        var duration: TextView = itemView.findViewById(R.id.element_duration),
        var vuMeterView: VuMeterView = itemView.findViewById(R.id.element_vu_meter_view),
        var filterView: View = itemView.findViewById(R.id.element_filter_view)) : RecyclerView.ViewHolder(itemView)

    private var tracksWithDrawables = ArrayList<Pair<TrackModel, Drawable?>>()

    private val transformation = RequestOptions.bitmapTransform(
        MultiTransformation(
            CropTransformation(300, 300, CropTransformation.CropType.CENTER),
            RoundedCornersTransformation(20, 0)
        )).priority(Priority.HIGH)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.track_element, parent, false))
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder.layoutPosition == tracksWithDrawables.size - 1) {
            eventListener?.onLastViewAttached(this, holder.layoutPosition)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trackWithDrawable = tracksWithDrawables[position]

        val track = trackWithDrawable.first
        val drawable = trackWithDrawable.second

        holder.title.text = track.title
        holder.authors.text = track.artist

        val photo = track.photo?.small ?: drawable

        Glide.with(context)
            .load(photo)
            .apply(transformation)
            .into(holder.image)

        holder.duration.text = StringUtil.getDurationFromSeconds(track.duration)

        if (!track.isEnabled) {
            holder.mainLayout.isEnabled = false
            holder.filterView.setBackgroundColor(0x55CCCCCC)
            return
        } else {
            holder.mainLayout.isEnabled = true
            holder.filterView.background = null
        }
        holder.image.clearColorFilter()
        holder.vuMeterView.visibility = View.GONE

        if (currentPlayingId == track.id) {
            holder.vuMeterView.visibility = View.VISIBLE
            if (isPaused) holder.vuMeterView.pause() else holder.vuMeterView.resume(true)
            holder.image.setColorFilter(ColorUtils.setAlphaComponent(Color.BLACK, 100))
            holder.mainLayout.setOnClickListener { eventListener?.onPauseEvent(isPaused) }
            return
        }

        holder.mainLayout.setOnClickListener {
            eventListener?.onItemSelected(track.id)
        }
    }

    override fun getItemCount(): Int {
        return tracksWithDrawables.size
    }

    fun addData(tracks: MutableList<TrackModel>) {
        tracks.forEach {
            tracksWithDrawables.add(Pair(it, null))
        }
        loadPhotos()
    }

    fun setData(tracks: MutableList<TrackModel>) {
        tracksWithDrawables.clear()
        tracks.forEach {
            tracksWithDrawables.add(Pair(it, null))
        }
        loadPhotos()
    }

    private fun getIndexById(id: String): Int {
        val i = tracksWithDrawables.indexOfFirst {
            it.first.id == id
        }
        return if (i == -1 || i >= itemCount) 0 else i
    }

    private fun loadPhotos() {
        tracksWithDrawables.forEachIndexed { index, pair ->
            tracksWithDrawables[index] = Pair(pair.first, trackDefaultImage)
        }
        notifyDataSetChanged()
    }

    fun getTrackList(): MutableList<TrackModel> {
        return tracksWithDrawables.map { it.first } as MutableList<TrackModel>
    }

    interface EventListener {
        fun onLastViewAttached(adapter: TracksAdapter, position: Int)
        fun onPauseEvent(isPaused: Boolean)
        fun onItemSelected(trackId: String)
    }
}
