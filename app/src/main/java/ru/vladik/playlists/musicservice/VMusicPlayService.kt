package ru.vladik.playlists.musicservice

import android.app.*
import android.content.Intent
import android.os.*
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.view.KeyEvent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSource
import ru.vladik.playlists.dataclasses.MusicSource
import ru.vladik.playlists.dataclasses.tracks.TrackModel

class VMusicPlayService : MediaBrowserServiceCompat() {

    private lateinit var notificationManager: VladikNotificationManager
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var dataSourceFactory: DefaultDataSource.Factory
    private var isForeground = false

    private val mAudioAttributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    private val playerListener = PlayerEventListener()

    var musicSource: MusicSource = MusicSource()

    val player: ExoPlayer by lazy {
        ExoPlayer.Builder(this).build().apply {
            setAudioAttributes(mAudioAttributes, true)
            setHandleAudioBecomingNoisy(true)
            addListener(playerListener)
            playWhenReady = true
        }
    }


    companion object {
        const val ACTION_BIND = "ru.vladik.action.ACTION_BIND"
    }


    private fun hideNotification() {
        notificationManager.hideNotification()
        player.stop()
        player.clearMediaItems()
    }

    override fun onCreate() {
        super.onCreate()

        mediaSession = MediaSessionCompat(this, "Vmusic")
        sessionToken = mediaSession.sessionToken
        mediaSession.isActive = true
        notificationManager = VladikNotificationManager(this, mediaSession.sessionToken, NotificationListener())
        dataSourceFactory = DefaultDataSource.Factory(this)

        player.addListener(playerListener)
        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlayer(player)

        mediaSessionConnector.setQueueNavigator(QueueNavigator(mediaSession))

        mediaSessionConnector.mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS)
        notificationManager.showNotificationForPlayer(player)
    }

    fun prepareAndPlay(
        itemToPlay: TrackModel? = null,
        playWhenReady: Boolean = false,
        tracks: MutableList<out TrackModel>? = null,
        ) {
        if (tracks != null) musicSource.musicList = tracks as ArrayList<TrackModel>
        val currentTrackIndex = if (itemToPlay == null) 0 else musicSource.musicList.indexOf(itemToPlay)
        if (player.playbackLooper.thread.isAlive) {
            player.prepare()
            player.setMediaItems(musicSource.toMediaItems(), currentTrackIndex, 0)
            player.seekTo(currentTrackIndex, 0)
            player.playWhenReady = playWhenReady
        }
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot {
        return BrowserRoot("bebra", null)
    }

    override fun onLoadChildren(parentId: String,
                                result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            if (intent.hasExtra(Intent.EXTRA_KEY_EVENT)) {
                val keyEvent = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
                when (keyEvent?.keyCode) {
                    null -> {}
                    KeyEvent.KEYCODE_MEDIA_STOP -> {
                        hideNotification()
                    }
                }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        hideNotification()
        player.release()
        player.removeListener(playerListener)
    }

    override fun onBind(intent: Intent?): IBinder? {
        if(intent?.action == ACTION_BIND) {
            return LocalBinder()
        }
        return super.onBind(intent)
    }

    inner class LocalBinder : Binder() {
        fun getService() : VMusicPlayService {
            return this@VMusicPlayService
        }
    }

    private inner class PlayerEventListener : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {
                Player.STATE_BUFFERING,
                Player.STATE_READY -> {
                    notificationManager.showNotificationForPlayer(player)
                }
                else -> {
                    notificationManager.hideNotification()
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Toast.makeText(this@VMusicPlayService, error.errorCodeName, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private inner class QueueNavigator(
        mediaSession: MediaSessionCompat
    ) : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return musicSource.musicList[windowIndex].toBrowserMediaItem().description
        }
    }

    private inner class NotificationListener : PlayerNotificationManager.NotificationListener {
        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            hideNotification()
            super.onNotificationCancelled(notificationId, dismissedByUser)
            if (isForeground) {
                stopForeground(true)
                isForeground = false
            }
        }

        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            super.onNotificationPosted(notificationId, notification, ongoing)
            if (!isForeground && ongoing) {
                ContextCompat.startForegroundService(this@VMusicPlayService,
                    Intent(this@VMusicPlayService, this@VMusicPlayService::class.java))
                startForeground(notificationId, notification)
                isForeground = true
            }
        }
    }


}

