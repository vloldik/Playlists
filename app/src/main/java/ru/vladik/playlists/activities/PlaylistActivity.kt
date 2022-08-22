package ru.vladik.playlists.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.ColorFilterTransformation
import ru.vladik.playlists.R
import ru.vladik.playlists.adapters.recycler_view.TracksAdapter
import ru.vladik.playlists.constants.INTENT_IMAGE
import ru.vladik.playlists.constants.INTENT_PLAYLIST
import ru.vladik.playlists.dataclasses.albums.AlbumModel
import ru.vladik.playlists.dataclasses.tracks.TrackModel
import ru.vladik.playlists.musicservice.VPlayServiceConnection
import ru.vladik.playlists.utils.*
import ru.vladik.playlists.utils.loaders.TrackLoader
import ru.vladik.playlists.views.BottomPlayerView
import kotlin.math.abs


class PlaylistActivity : AppCompatActivity() {

    private lateinit var playlist: AlbumModel
    private lateinit var tracksLoader: TrackLoader
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var toolbarImageView: ImageView
    private lateinit var trackCountTextview: TextView
    private lateinit var loadingContainer: ViewGroup
    private lateinit var playerView: BottomPlayerView

    private var isDrawableSet = false
    private var isLoading = false
    set(value) {
        field = value
        if (this::loadingContainer.isInitialized)
        LayoutHelper.setLoading(loadingContainer, isLoading)
    }
    private var isFullLoaded = false
    private var playerEventListener: PlayerListener? = null

    private val onLastViewAttachedListener = EventListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)


        playlist = intent.getSerializableExtra(INTENT_PLAYLIST) as AlbumModel

        tracksLoader = TrackLoader.Builder(this).apply {
            setService(playlist.service)
            setPlaylistId(playlist.id)
            setOwnerId(playlist.ownerId)
        }.build()

        tracksRecyclerView = findViewById(R.id.tracks_recycler_view)
        playerView = findViewById(R.id.player_view)
        toolbarImageView = findViewById(R.id.expanded_image)
        trackCountTextview = findViewById(R.id.track_count)
        loadingContainer = findViewById(R.id.loading_container)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val appBarLayout = findViewById<AppBarLayout>(R.id.app_bar)
        val toolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)
        val descriptionTextview = findViewById<TextView>(R.id.description)

        toolbarLayout.contentScrim = null

        VPlayServiceConnection.getInstance(this).registerInitListenerForOneTime {
            playerView.player = it.player
            it.player?.let { player ->
                playerEventListener = PlayerListener(player)
                player.addListener(playerEventListener!!)
            }
        }

        playerView.setOnClickListener {
            startActivity(Intent(this, TrackActivity::class.java))
        }

        if (playlist.count >= 0) {
            val trackCountText =
                "${playlist.count} ${
                    StringUtil.getTrackStringByNum(this, playlist.count.toString())
                }"

            trackCountTextview.text = trackCountText
        }
        descriptionTextview.text = playlist.description

        toolbar.title = playlist.title
        toolbar.setNavigationIcon(R.drawable.arrow_back)
        toolbar.navigationIcon?.setTint(Color.WHITE)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val imageFromIntent = intent?.getParcelableExtra<Bitmap>(INTENT_IMAGE)
        if (imageFromIntent != null) {
            toolbarImageView.setImageDrawable(BitmapDrawable(resources, imageFromIntent))
        }

        tracksRecyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        tracksRecyclerView.layoutManager = LinearLayoutManager(this)
        tracksRecyclerView.adapter = TracksAdapter(this)


        if (playlist.photo != null) {
            getGlideBuilder(playlist.photo!!.large)
                .listener(DrawableListener(true))
                .into(toolbarImageView)
        }

        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { abl, verticalOffset ->
            run {
                descriptionTextview.alpha =
                    1 - abs(verticalOffset).toFloat() / abl.totalScrollRange
                trackCountTextview.alpha =
                    1 - abs(verticalOffset).toFloat() / abl.totalScrollRange
            }
        })

        loadTracks()
        (tracksRecyclerView.adapter as TracksAdapter).eventListener = onLastViewAttachedListener
    }

    private fun loadTracks() {
        if (isLoading) return
        isLoading = true
        tracksLoader.loadAll {
            val trackCountText =
                "${it.size} ${StringUtil.getTrackStringByNum(this, it.size.toString())}"

            trackCountTextview.text = trackCountText
            addData(it)
            isLoading = false
        }
    }

    private inner class EventListener : TracksAdapter.EventListener {
        override fun onLastViewAttached(adapter: TracksAdapter, position: Int) {
            if (position < playlist.count - 1) {
                loadTracks()
            }
        }

        override fun onPauseEvent(isPaused: Boolean) {
            playerView.dispatchPlayerCommand(Player.COMMAND_PLAY_PAUSE)
        }

        override fun onItemSelected(trackId: String) {
            val trackList = (tracksRecyclerView.adapter!! as TracksAdapter).getTrackList()
            val preparedTrackList = ArrayList<TrackModel>()
            trackList.forEach {track ->  if (!track.url.isNullOrEmpty()) preparedTrackList.add(track) }
            VPlayServiceConnection.getInstance(this@PlaylistActivity)
                .registerInitListenerForOneTime {
                    it.playTrackById(preparedTrackList, trackId)
                }
            return
        }
    }

    private inner class PlayerListener(private val player: Player) : Player.Listener {
        private val adapter
            get() = tracksRecyclerView.adapter as TracksAdapter?

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            if (player.currentMediaItem == null) {
                adapter?.currentPlayingId = ""
                return
            }
            adapter?.currentPlayingId = player.currentMediaItem!!.mediaId
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            adapter?.isPaused = !isPlaying
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun addData(tracks: MutableList<out TrackModel>) {
        if (isFullLoaded) return
        (tracksRecyclerView.adapter as TracksAdapter).addData(tracks as MutableList<TrackModel>)
        playerEventListener?.onMediaMetadataChanged(MediaMetadata.EMPTY)
    }

    @Suppress("UNCHECKED_CAST", "UNUSED")
    private fun setData(tracks: MutableList<out TrackModel>) {
        (tracksRecyclerView.adapter as TracksAdapter).setData(tracks as MutableList<TrackModel>)
        playerEventListener?.onMediaMetadataChanged(MediaMetadata.EMPTY)
    }


    @SuppressLint("CheckResult")
    private fun getGlideBuilder(src: Any, transformation: BlurTransformation? = null): RequestBuilder<Drawable> {
        return Glide.with(this).load(src).apply {
            transform(ColorFilterTransformation(0x66000000))
            if (transformation == null) return@apply
            transform(transformation)
        }

    }

    private inner class DrawableListener(private val isDrawableSet: Boolean) : RequestListener<Drawable> {


        override fun onResourceReady(
            resource: Drawable,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            if (this@PlaylistActivity.isDrawableSet) return true
            toolbarImageView.setImageDrawable(resource)
            val averageColor = VColorUtils.getAverageColor(resource.toBitmap())
            window.statusBarColor = averageColor
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

            this@PlaylistActivity.isDrawableSet = isDrawableSet
            return false
        }

        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean = false

    }
}