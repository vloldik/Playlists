package ru.vladik.playlists.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.vladik.playlists.R
import ru.vladik.playlists.musicservice.VPlayServiceConnection
import ru.vladik.playlists.views.FullscreenPlayerView

class TrackActivity : AppCompatActivity() {
    private lateinit var playerView: FullscreenPlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track)

        playerView = findViewById(R.id.player_view)

        VPlayServiceConnection.getInstance(this).registerInitListenerForOneTime {
            playerView.player = it.player
        }
        playerView.onNothingToShow = {
            playerView.subscribeOnCallbacks = false
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerView.subscribeOnCallbacks = false
    }
}