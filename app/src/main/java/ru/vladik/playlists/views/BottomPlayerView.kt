package ru.vladik.playlists.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.*
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.util.Util
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import ru.vladik.playlists.R

class BottomPlayerView : RelativeLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val maxUpdateIntervalMS = 1_000
    private val playerListener = PlayerListener()
    var subscribeOnCallbacks: Boolean = false
    set(value) {
        if (value == field) return
        if (value) subscribeOnCallbacks() else removeCallbacks()
        field = value
    }

    private val updateProgressAction: () -> Unit = this::updateProgress
    private val buttonNext: ImageButton
    private val buttonPlayPause: ImageButton
    private val imageView: ImageView
    private val titleTextView: TextView
    private val subtitleTextView: TextView
    private val playbackProgressBar: ProgressBar
    var player: ExoPlayer? = null
        set(value) {
            field = value
            onPlayerSet()
        }

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_player_view, this)
        buttonNext = view.findViewById(R.id.play_next_button)
        buttonPlayPause = view.findViewById(R.id.play_pause_button)
        imageView = view.findViewById(R.id.element_image)
        titleTextView = view.findViewById(R.id.element_title)
        subtitleTextView = view.findViewById(R.id.element_description)
        playbackProgressBar = view.findViewById(R.id.playback_progressbar)

        buttonPlayPause.setOnClickListener {
            dispatchPause()
        }
    }

    private fun dispatchPause() {
        if (player != null && player!!.isPlaying) player?.pause() else player?.play()
    }

    private fun hide() {
        visibility = GONE
    }

    private fun show() {
        visibility = VISIBLE
    }

    fun close() {
        player?.pause()
        player?.clearMediaItems()
        hide()
    }

    private fun onPlayerSet() {
        if (player == null) {
            hide()
        } else {
            subscribeOnCallbacks = true
            applyMetadata(player!!.mediaMetadata)
            applyIsPlaying(player!!.isPlaying)
            updateProgress()
        }
    }

    private inner class PlayerListener : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
        }

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            applyMetadata(mediaMetadata)
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            applyIsPlaying(isPlaying)
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            updateProgress()
        }
    }

    private fun applyIsPlaying(isPlaying: Boolean) {
        buttonPlayPause.setImageResource(if (isPlaying) R.drawable.pause else R.drawable.play)
    }

    private fun applyMetadata(mediaMetadata: MediaMetadata) {
        if (player == null || player!!.mediaItemCount < 1) {
            hide()
        } else {
            show()
        }
        titleTextView.text = mediaMetadata.title
        subtitleTextView.text = mediaMetadata.artist
        Glide.with(context.applicationContext)
            .asDrawable()
            .load(mediaMetadata.artworkUri)
            .transform(RoundedCornersTransformation(10, 0))
            .into(imageView)

        if (player != null && player!!.hasNextMediaItem()) {
            buttonNext.isEnabled = true
            buttonNext.setOnClickListener {
                player!!.seekToNext()
            }
        } else {
            buttonNext.isEnabled = false
        }
    }

    private fun removeCallbacks() {
        player?.removeListener(playerListener)
    }

    private fun subscribeOnCallbacks() {
        player?.addListener(playerListener)
    }

    private fun updateProgress() {
        if (!isVisible) {
            return
        }
        val player: Player? = player
        var position: Long = 0
        var duration: Long = 0
        if (player != null) {
            position = player.contentPosition
            duration = player.contentDuration
        }

        playbackProgressBar.progress = ((position.toDouble() / duration.toDouble()) * 100).toInt()

        removeCallbacks(updateProgressAction)
        val playbackState = player?.playbackState ?: Player.STATE_IDLE
        if (player != null && player.isPlaying) {
            var mediaTimeDelayMs = maxUpdateIntervalMS.toLong()

            val mediaTimeUntilNextFullSecondMs = 1000 - position % 1000
            mediaTimeDelayMs = mediaTimeDelayMs.coerceAtMost(mediaTimeUntilNextFullSecondMs)

            val playbackSpeed = player.playbackParameters.speed
            var delayMs =
                if (playbackSpeed > 0) (mediaTimeDelayMs / playbackSpeed).toLong()
                else maxUpdateIntervalMS.toLong()

            delayMs = Util.constrainValue(
                delayMs,
                maxUpdateIntervalMS.toLong(),
                maxUpdateIntervalMS.toLong()
            )
            postDelayed(updateProgressAction, delayMs)
        } else if (playbackState != Player.STATE_ENDED && playbackState != Player.STATE_IDLE) {
            postDelayed(
                updateProgressAction,
                maxUpdateIntervalMS.toLong()
            )
        }
    }

    fun dispatchPlayerCommand(command: Int) {
        when(command) {
            Player.COMMAND_PLAY_PAUSE -> {
                dispatchPause()
            }
        }
    }
}