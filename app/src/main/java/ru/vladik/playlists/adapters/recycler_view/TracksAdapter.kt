package ru.vladik.playlists.adapters.recycler_view

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import ru.vladik.playlists.R
import ru.vladik.playlists.api.vk.getAudiosWithUrl
import ru.vladik.playlists.api.vk.models.Track
import ru.vladik.playlists.dataclasses.TrackModel
import ru.vladik.playlists.dataclasses.BasicTrackWithDrawable
import ru.vladik.playlists.extensions.ApiExtensions.toPlayableTrackList
import ru.vladik.playlists.services.VMediaButtonReciever
import ru.vladik.playlists.utils.AsyncUtils
import ru.vladik.playlists.utils.DrawableUtil
import ru.vladik.playlists.utils.Constants
import ru.vladik.playlists.utils.StringUtil
import kotlin.collections.ArrayList

class TracksAdapter(val context: Context) : RecyclerView.Adapter<TracksAdapter.ViewHolder>() {

    private val trackDefaultImage: Drawable = AppCompatResources.getDrawable(context, R.drawable.track)!!

    data class ViewHolder(var itemView: View, val image: ImageView = itemView.findViewById(R.id.element_image),
        var title: TextView = itemView.findViewById(R.id.element_title),
        var authors: TextView = itemView.findViewById(R.id.element_description),
        var mainLayout: View = itemView.findViewById(R.id.element_main_layout),
        var duration: TextView = itemView.findViewById(R.id.element_duration),
        var filterView: View = itemView.findViewById(R.id.element_filter_view)) : RecyclerView.ViewHolder(itemView)

    private var tracksWithDrawables: MutableList<BasicTrackWithDrawable> = ArrayList()
    private var tracks: MutableList<TrackModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.track_element, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val track = tracksWithDrawables[position]
        holder.title.text = track.tName
        holder.authors.text = track.tAuthor
        holder.image.setImageDrawable(track.drawable)
        holder.duration.text = StringUtil.getDurationFromSeconds(track.tDuration)
        if (tracks[position] is Track) {
            val vkTrack = tracks[position] as Track
            if (vkTrack.contentRestricted == 1) {
                holder.mainLayout.isEnabled = false
                holder.filterView.setBackgroundColor(0x55CCCCCC)
            } else {
                holder.mainLayout.isEnabled = true
                holder.filterView.background = null
            }
            holder.mainLayout.setOnClickListener {
                AsyncUtils.asyncLaunch({
                    val response = Constants.vkService!!.getAudiosWithUrl(tracks
                    as MutableList<Track>).execute().body()
                    if (response.error == null) {
                        Constants.musicPlayService?.player!!.trackList = response.response.toPlayableTrackList()
                        Constants.musicPlayService?.player!!.currentTrack = response.response.toPlayableTrackList()[position]
                        Constants.musicPlayService?.player!!.play()
                    }

                })
            }
        }
    }

    override fun getItemCount(): Int {
        return tracksWithDrawables.size
    }

    fun refreshData(tracks: MutableList<TrackModel>) {
        for (item in tracks) {
            val trackWithDrawable = item.toTrackWithDrawable()
            trackWithDrawable.drawable = trackDefaultImage
            this.tracks = tracks
            this.tracksWithDrawables.add(trackWithDrawable)
            if (item.getPhoto() != null) {
                Glide.with(this.context).asDrawable().load(item.getPhoto()!!.small)
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(resource: Drawable,
                                                     transition: Transition<in Drawable>?) {
                            trackWithDrawable.drawable = resource
                            notifyItemChanged(this@TracksAdapter
                                .tracksWithDrawables.indexOf(trackWithDrawable))
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })
            }
        }
        notifyDataSetChanged()
    }
}
