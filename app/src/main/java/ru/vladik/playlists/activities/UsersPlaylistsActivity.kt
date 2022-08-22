package ru.vladik.playlists.activities

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.vladik.playlists.R
import ru.vladik.playlists.adapters.OnLastElementAttachedListener
import ru.vladik.playlists.adapters.recycler_view.PlaylistsAdapter
import ru.vladik.playlists.constants.INTENT_PLATFORM
import ru.vladik.playlists.dataclasses.albums.AlbumModel
import ru.vladik.playlists.utils.AdapterDataContainer
import ru.vladik.playlists.utils.AnimationEndListener
import ru.vladik.playlists.utils.LayoutHelper
import ru.vladik.playlists.utils.loaders.AlbumLoader
import kotlin.collections.ArrayList

class UsersPlaylistsActivity : AppCompatActivity() {

    private val dataContainer = AdapterDataContainer<AlbumModel>(ArrayList())

    private var platformId = 1L
    private var lastElementPosition = 0

    private val animationTime = 250L

    private lateinit var animationUp: Animation
    private lateinit var animationDown: Animation


    private lateinit var playlistsRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var playlistsAdapter: PlaylistsAdapter
    private lateinit var loader: AlbumLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_playlist_list)

        val distance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40F, resources.displayMetrics)

        animationUp = TranslateAnimation(0F,0F, 0F, -distance)
        animationDown = TranslateAnimation(0F,0F, -distance, 0F)

        animationUp.duration = animationTime
        animationUp.fillAfter = true
        animationDown.duration = animationTime
        animationDown.fillAfter = true

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.arrow_back)
        toolbar.navigationIcon?.setTint(Color.BLACK)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        platformId = intent.getLongExtra(INTENT_PLATFORM, 1)
        initViews()
        loader = AlbumLoader.getAlbumLoaderForPlatform(this, platformId)
        loader.callback = { albums ->
            resetAnimations()
            swipeRefreshLayout.startAnimation(animationDown)
            swipeRefreshLayout.postDelayed({
                dataContainer.data += albums
                dataContainer.displayData += albums
                playlistsAdapter.notifyItemRangeInserted(lastElementPosition + 1, albums.size)
            }, animationTime)
        }
        loader.nextPage()
    }

    private fun initViews() {
        playlistsRecyclerView = findViewById(R.id.playlist_recycler_view)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        playlistsRecyclerView.layoutManager =
            GridLayoutManager(this, resources.displayMetrics.widthPixels/
                    LayoutHelper.dipToPx(150F, this).toInt())
        playlistsAdapter = PlaylistsAdapter(dataContainer)
        playlistsRecyclerView.adapter = playlistsAdapter
        playlistsAdapter.lastElementAttachedListener = object : OnLastElementAttachedListener {
            override fun onLastViewAttached(position: Int) {
                lastElementPosition = position
                if (!loader.hasNext) return
                resetAnimations()
                swipeRefreshLayout.startAnimation(animationUp)
                swipeRefreshLayout.postDelayed({
                    loader.nextPage()
                }, animationTime)
            }
        }
    }

    fun resetAnimations() {
        animationUp.cancel()
        animationUp.reset()
        animationDown.cancel()
        animationDown.reset()
    }
}