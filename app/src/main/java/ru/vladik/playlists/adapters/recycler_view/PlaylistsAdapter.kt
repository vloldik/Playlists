package ru.vladik.playlists.adapters.recycler_view

import android.app.ActionBar
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import ru.vladik.playlists.R
import ru.vladik.playlists.activities.PlaylistActivity
import ru.vladik.playlists.constants.Strings
import ru.vladik.playlists.dataclasses.BasicPlaylist
import ru.vladik.playlists.dataclasses.PlaylistWithDrawable
import ru.vladik.playlists.utils.ColorUtils
import ru.vladik.playlists.utils.StringUtil

class PlaylistsAdapter(private val mContext: Context) : RecyclerView.Adapter<PlaylistsAdapter.ViewHolder>() {

    private var playlists: MutableList<PlaylistWithDrawable> = ArrayList()
    private val defaultPlaylistDrawable = AppCompatResources.getDrawable(mContext, R.drawable.playlist)!!
    private val defaultPlaylistColor = ColorUtils.checkColorTooWhite(ColorUtils.getAverageColor(defaultPlaylistDrawable.toBitmap()))

    data class ViewHolder(var itemView: View,
                          var titleTextView: TextView = itemView.findViewById(R.id.element_title),
                          var descriptionTextView: TextView = itemView.findViewById(R.id.element_description),
                          var trackCountTextView: TextView = itemView.findViewById(R.id.element_track_count),
                          var imageView: ImageView = itemView.findViewById(R.id.element_image),
                          var mainLayout: ViewGroup = itemView.findViewById(R.id.element_main_layout),)
        : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: ViewGroup = LayoutInflater.from(parent.context).inflate(R.layout.rounded_album_element_with_image,
            parent, false) as ViewGroup
        view.layoutParams.width = ActionBar.LayoutParams.MATCH_PARENT

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.titleTextView.text = playlist.title
        holder.descriptionTextView.text = playlist.description
        holder.imageView.setImageDrawable(defaultPlaylistDrawable)

        val trackCountText = playlist.count + " " + StringUtil.getTrackStringByNum(mContext, playlist.count)
        holder.trackCountTextView.text = trackCountText

        if (playlist.drawable != null && playlist.averageDrawableColor != null) {
            Glide.with(mContext)
                .load(playlist.drawable)
                .transform(RoundedCornersTransformation(10, 0))
                .into(holder.imageView)
            holder.mainLayout.setBackgroundColor(playlist.averageDrawableColor!!)
        } else {
            holder.mainLayout.setBackgroundColor(defaultPlaylistColor)
        }

        holder.mainLayout.setOnClickListener {
            val intent = Intent(mContext, PlaylistActivity::class.java)
            intent.putExtra(Strings.INTENT_PLAYLIST, playlist.toPlaylist())
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun loadData(playlists: MutableList<BasicPlaylist>) {
        for (item in playlists) {
            val playlistWithDrawable = item.toPlaylistWithDrawable()
            playlistWithDrawable.drawable = defaultPlaylistDrawable
            this.playlists.add(playlistWithDrawable)
            if (item.photo != null) {
                Glide.with(mContext).asDrawable().load(item.photo!!.small).into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable,
                                                 transition: Transition<in Drawable>?) {
                        playlistWithDrawable.drawable = resource
                        playlistWithDrawable.averageDrawableColor = ColorUtils.checkColorTooWhite(playlistWithDrawable.getColor()!!)
                        notifyItemChanged(this@PlaylistsAdapter.playlists.indexOf(playlistWithDrawable))
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
            }
        }
        notifyDataSetChanged()
    }
}
