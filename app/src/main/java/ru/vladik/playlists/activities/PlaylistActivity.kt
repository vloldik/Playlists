package ru.vladik.playlists.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.ColorFilterTransformation
import ru.vladik.playlists.R
import ru.vladik.playlists.adapters.recycler_view.TracksAdapter
import ru.vladik.playlists.constants.Strings
import ru.vladik.playlists.dataclasses.BasicPlaylist
import ru.vladik.playlists.dataclasses.PlaylistWithDrawable
import ru.vladik.playlists.dataclasses.TrackModel
import ru.vladik.playlists.utils.*
import kotlin.math.abs


class PlaylistActivity : AppCompatActivity() {
    lateinit var playlist: PlaylistWithDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)

        playlist = (intent.getSerializableExtra(Strings.INTENT_PLAYLIST) as BasicPlaylist).toPlaylistWithDrawable()

        val toolbarImageView = findViewById<ImageView>(R.id.expanded_image)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val appBarLayout = findViewById<AppBarLayout>(R.id.app_bar)
        val toolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)
        val tracksRecyclerView = findViewById<RecyclerView>(R.id.tracks_recycler_view)
        val navigationClose = findViewById<View>(R.id.navigation_close)

        val trackCountTextview = findViewById<TextView>(R.id.track_count)
        val descriptionTextview = findViewById<TextView>(R.id.description)

        val trackCountText = playlist.count + " " + StringUtil.getTrackStringByNum(this, playlist.count)

        navigationClose.setOnClickListener {
            onBackPressed()
        }

        androidx.media.R.layout.notification_template_media

        trackCountTextview.text = trackCountText
        descriptionTextview.text = playlist.description

        toolbarImageView.setImageDrawable(ColorDrawable(Color.GRAY))

        toolbar.title = playlist.title

        tracksRecyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        tracksRecyclerView.layoutManager = LinearLayoutManager(this)
        tracksRecyclerView.adapter = TracksAdapter(this)

        if (playlist.photo != null) {
            Glide.with(this@PlaylistActivity).load(playlist.photo!!.large)
                .transform(BlurTransformation())
                .transform(ColorFilterTransformation(0x66000000))
                .into(toolbarImageView)
            toolbarLayout.contentScrim = null
        }

        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener {
            abl, verticalOffset ->
            run {
                descriptionTextview.alpha =
                    1 - abs(verticalOffset).toFloat() / abl.totalScrollRange
                trackCountTextview.alpha =
                    1 - abs(verticalOffset).toFloat() / abl.totalScrollRange
            }
        })

        when(playlist.service) {
            AppServices.vk -> {
                val tracks: MutableList<TrackModel> = ArrayList()
                AsyncUtils.asyncLaunch({
                    val vkTracks = Constants.vkService?.getTracks(albumId = playlist.id.toLong())?.execute()?.body()
                    vkTracks?.response?.items?.let {
                        for (item in it) {
                            tracks.add(item as TrackModel)
                        }
                    }
                    Log.d("main", tracks.toString())
                }, {
                    (tracksRecyclerView.adapter as TracksAdapter).refreshData(tracks)
                })

            }
        }

    }
}