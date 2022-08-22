package ru.vladik.playlists.adapters.recycler_view

import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.wajahatkarim3.easyflipview.EasyFlipView
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.ColorFilterTransformation
import jp.wasabeef.glide.transformations.CropTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import ru.vladik.playlists.R
import ru.vladik.playlists.activities.PlaylistActivity
import ru.vladik.playlists.adapters.OnLastElementAttachedListener
import ru.vladik.playlists.constants.INTENT_IMAGE
import ru.vladik.playlists.constants.INTENT_PLAYLIST
import ru.vladik.playlists.dataclasses.albums.AlbumModel
import ru.vladik.playlists.utils.AdapterDataContainer
import ru.vladik.playlists.utils.BacksideFlipViewInflater
import ru.vladik.playlists.utils.StringUtil
import ru.vladik.playlists.views.BackSideActionsView

class PlaylistsAdapter(var dataContainer: AdapterDataContainer<AlbumModel>, val compact: Boolean = false)
    : RecyclerView.Adapter<PlaylistsAdapter.ViewHolder>() {

    var lastElementAttachedListener: OnLastElementAttachedListener = OnLastElementAttachedListener.Default()

    private lateinit var defaultPlaylistDrawable: Drawable

    private val transformation =
         RequestOptions.bitmapTransform(
            MultiTransformation(
                CropTransformation(300, 300, CropTransformation.CropType.CENTER),
                ColorFilterTransformation(ColorUtils.setAlphaComponent(Color.BLACK, 140)),
                BlurTransformation(2, 1)
            )).priority(Priority.HIGH)



    inner class ViewHolder(
        private var itemView: View,
        var backsideImage: ImageView = itemView.findViewById(R.id.element_background_image_back),
        var backsideLayout: CardView = itemView.findViewById(R.id.back_side_layout),
        var flipView: EasyFlipView = itemView.findViewById(R.id.flip_view1),
        var titleTextView: TextView = itemView.findViewById(R.id.element_title),
        var descriptionTextView: TextView = itemView.findViewById(R.id.element_description),
        var trackCountTextView: TextView = itemView.findViewById(R.id.element_track_count),
        var mainLayout: CardView = itemView.findViewById(R.id.element_main_layout),
        var contentLocked: TextView? = if (!compact) itemView.findViewById(R.id.element_content_locked) else null,
        var backgroundImage: ImageView = itemView.findViewById(R.id.element_background_image))
        : RecyclerView.ViewHolder(itemView) {
            val context: Context
                get() = titleTextView.context
        }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: ViewGroup = LayoutInflater.from(parent.context).inflate(
             R.layout.album_element_flip,
            parent, false) as ViewGroup
        if (!compact) view.layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT
        return ViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        Glide.with(recyclerView.context)
            .load(R.drawable.default_playlist_bg)
            .apply(transformation)
            .into(DefaultDrawableSetter())
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        if (holder.layoutPosition == itemCount - 1) {
            lastElementAttachedListener.onLastViewAttached(holder.layoutPosition)
        }
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.flipView.isBackSide) holder.flipView.flipTheView(false)
        val playlist = dataContainer.displayData[position]
        val context = holder.context

        holder.titleTextView.text = playlist.title
        holder.descriptionTextView.text = playlist.description

        if (playlist.count >= 0) {
            val trackCountText =
                "${playlist.count} ${
                    StringUtil.getTrackStringByNum(context, playlist.count.toString())
                }"
            holder.trackCountTextView.text = trackCountText
        } else holder.trackCountTextView.text = ""

        if (this::defaultPlaylistDrawable.isInitialized) holder.backgroundImage.setImageDrawable(defaultPlaylistDrawable)

        if (!playlist.isEnabled) {
            holder.contentLocked?.visibility = View.VISIBLE
            holder.mainLayout.setOnClickListener {  }
            return
        }

        if (playlist.photo != null) {
            Glide.with(context)
                .load(playlist.photo!!.medium)
                .apply(transformation)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.backgroundImage)
        }

        val imageView1 = holder.backsideImage.apply {
            setImageDrawable(holder.backgroundImage.drawable)
            scaleX = -1F
        }
        if (playlist.photo != null) {
            Glide.with(context)
                .load(playlist.photo!!.medium)
                .apply(transformation)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView1)
        }

        holder.contentLocked?.visibility = View.GONE
        holder.mainLayout.setOnClickListener {
            val intent = Intent(context, PlaylistActivity::class.java)
            intent.putExtra(INTENT_PLAYLIST, playlist)
            intent.putExtra(INTENT_IMAGE, holder.backgroundImage.drawable.toBitmap(200, 200))
            context.startActivity(intent)
        }

        holder.mainLayout.setOnLongClickListener {
            holder.flipView.flipTheView(true)
            return@setOnLongClickListener true
        }

        val backInflater = BacksideFlipViewInflater.Builder(
            {holder.flipView.flipTheView(true)}, holder.backsideLayout)
            .addAction("назад", R.drawable.return_ic) {holder.flipView.flipTheView()}
            .addAction("назад", R.drawable.ic_baseline_delete_24) {}
            .addAction("назад", R.drawable.close) {}
            .addAction("назад", R.drawable.close) {}
            .addAction("назад", R.drawable.close) {}
            .addAction("назад", R.drawable.play) {}
            .build()
        backInflater.getInflatedView()
    }

    private inner class DefaultDrawableSetter: CustomTarget<Drawable>()  {
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            defaultPlaylistDrawable = resource
            notifyDataSetChanged()
        }

        override fun onLoadCleared(placeholder: Drawable?) {
        }
    }

    override fun getItemCount(): Int {
        return dataContainer.displayData.size
    }
}
