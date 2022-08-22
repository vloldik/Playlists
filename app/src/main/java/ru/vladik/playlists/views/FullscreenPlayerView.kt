package ru.vladik.playlists.views

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.*
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.exoplayer2.util.Util
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import ru.vladik.playlists.R
import ru.vladik.playlists.utils.AsyncUtils
import ru.vladik.playlists.utils.StringUtil
import ru.vladik.playlists.utils.VColorUtils
import android.R.attr.*
import com.google.android.exoplayer2.*

class FullscreenPlayerView : LinearLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        this.orientation = VERTICAL
    }

    var onNothingToShow: (() -> Unit) = {}

    private var isSeekBarSeeking = false

    private val maxUpdateIntervalMS = 1_000

    private val playerListener = PlayerListener()
    private val updateProgressAction: () -> Unit = this::updateProgress
    private val buttonNext: ImageButton
    private val buttonPrevious: ImageButton
    private val buttonShuffle: ImageButton
    private val buttonPlayPause: ImageButton
    private val imageView: ImageView
    private val titleTextView: TextView
    private val nowDurationTextView: TextView
    private val totalDurationTextView: TextView
    private val subtitleTextView: TextView
    private val playbackSeekBar: SeekBar

    var player: ExoPlayer? = null
        set(value) {
            field = value
            onPlayerSet()
        }

    var subscribeOnCallbacks: Boolean = false
        set(value) {
            if (value == subscribeOnCallbacks) return
            if (value) subscribeOnCallbacks() else removeCallbacks()
            field = value
        }

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.fullscreen_player_view, this)
        buttonNext = view.findViewById(R.id.play_next_button)
        buttonPrevious = view.findViewById(R.id.play_previous_button)
        buttonPlayPause = view.findViewById(R.id.play_pause_button)
        buttonShuffle = view.findViewById(R.id.shuffle_button)
        imageView = view.findViewById(R.id.element_image)
        titleTextView = view.findViewById(R.id.element_title)
        nowDurationTextView = view.findViewById(R.id.now_duration_text)
        totalDurationTextView = view.findViewById(R.id.total_duration_text)
        subtitleTextView = view.findViewById(R.id.element_description)
        playbackSeekBar = view.findViewById(R.id.playback_progressbar)
        if (context is Activity) {
            val w = (context as Activity).window
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        buttonNext.setOnClickListener {
            player!!.seekToNext()
        }

        buttonPrevious.setOnClickListener {
            player!!.seekToPrevious()
        }

        buttonShuffle.setOnClickListener {
            player!!.shuffleModeEnabled = !player!!.shuffleModeEnabled
        }

        playbackSeekBar.setOnSeekBarChangeListener(SeekBarListener())

        buttonPlayPause.setOnClickListener {
            dispatchPause()
        }
    }

    private fun setUIColor(color: Int) {

        val buttonTintList = ColorStateList(
            arrayOf(
                /*pressed*/ intArrayOf(state_pressed),
                /*disabled*/ intArrayOf(-state_enabled),
                /*default*/ intArrayOf()),
            intArrayOf(
                /*pressed*/ ColorUtils.setAlphaComponent(color, 160),
                /*disabled*/ ColorUtils.setAlphaComponent(color, 109),
                /*default*/ color))

        buttonShuffle.imageTintList = ColorStateList.valueOf(color)
        playbackSeekBar.progressBackgroundTintList = ColorStateList.valueOf(color)
        playbackSeekBar.progressTintList =
            ColorStateList.valueOf(ColorUtils.blendARGB(Color.WHITE, color, 0.5f))
        playbackSeekBar.thumbTintList = buttonTintList
        buttonPrevious.imageTintList = buttonTintList
        buttonPlayPause.imageTintList = buttonTintList
        buttonNext.imageTintList = buttonTintList

        titleTextView.setTextColor(color)
        subtitleTextView.setTextColor(ColorUtils.setAlphaComponent(color, 200))
        nowDurationTextView.setTextColor(color)
        totalDurationTextView.setTextColor(color)
    }

    private fun dispatchPause() {
        if (player != null && player!!.isPlaying) player?.pause() else player?.play()
    }

    private fun onPlayerSet() {
        if (player == null) {
            onNothingToShow()
            return
        }
        subscribeOnCallbacks = true
        applyMetadata(player!!.mediaMetadata)
        applyIsPlaying(player!!.isPlaying)
        applyShuffleMod(player!!.shuffleModeEnabled)
        updateProgress()
    }

    private fun applyShuffleMod(shuffleModeEnabled: Boolean) {
        if (!shuffleModeEnabled) {
            buttonShuffle.imageAlpha = 0x66
        } else {
            buttonShuffle.imageAlpha = 0xFF
        }
        checkPrevNextButtonEnabled()
    }

    private fun applyContentSeek(seekBar: SeekBar) {
        var duration = 0L
        if (player != null && player!!.contentDuration != C.TIME_UNSET)
            duration = player!!.contentDuration
        val position = (duration/100)*seekBar.progress

        player?.seekTo(position)
    }

    private fun applyIsPlaying(isPlaying: Boolean) {
        buttonPlayPause.setImageResource(if (isPlaying) R.drawable.pause else R.drawable.play)
    }

    private fun applyMetadata(mediaMetadata: MediaMetadata) {
        if (player == null || player!!.mediaItemCount < 1) {
            onNothingToShow()
            return
        }
        titleTextView.text = mediaMetadata.title
        subtitleTextView.text = mediaMetadata.artist
        player!!.contentDuration
        val src: Any = mediaMetadata.artworkUri ?: R.drawable.track
        Glide.with(context)
            .load(src)
            .transform(RoundedCornersTransformation(10, 0))
            .listener(ImageListener())
            .error(Glide.with(context)
                .load(R.drawable.track)
                .transform(RoundedCornersTransformation(10, 0))
                .listener(ImageListener())
                .into(imageView))
            .into(imageView)
        checkPrevNextButtonEnabled()
    }

    private fun checkPrevNextButtonEnabled() {
        buttonNext.isEnabled = player?.hasNextMediaItem() == true ||
                player?.shuffleModeEnabled == true
        buttonPrevious.isEnabled = player?.hasPreviousMediaItem() == true ||
                player?.shuffleModeEnabled == true
    }

    private fun removeCallbacks() {
        player?.removeListener(playerListener)
    }

    private fun subscribeOnCallbacks() {
        player!!.addListener(playerListener)
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

        if (!isSeekBarSeeking) {
            playbackSeekBar.progress = ((position.toDouble() / duration.toDouble()) * 100).toInt()
            nowDurationTextView.text = StringUtil.getDurationFromSeconds((position / 1000).toInt())
        }

        if (duration == C.TIME_UNSET) duration = 0

        totalDurationTextView.text = StringUtil.getDurationFromSeconds((duration/1000).toInt())

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

    private inner class ImageListener : RequestListener<Drawable> {

        private var firstTry = true

        override fun onResourceReady(
            resource: Drawable,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            lateinit var color: Color
            val colorInt = VColorUtils.getAverageColor(resource.toBitmap())
            lateinit var background: Drawable
            AsyncUtils.asyncLaunch({
                background = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(
                    ColorUtils.blendARGB(colorInt, Color.WHITE, 0.2f),
                    colorInt,
                    ColorUtils.blendARGB(colorInt, Color.BLACK, 0.2f)
                ))
                color = Color.valueOf(VColorUtils.getContrastColor(colorInt))
            }, {
                this@FullscreenPlayerView.background = background
                setUIColor(color.toArgb())
            })
            return false
        }

        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean = false
    }

    private inner class SeekBarListener : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (fromUser && !isSeekBarSeeking)
                applyContentSeek(seekBar)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            isSeekBarSeeking = true
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            isSeekBarSeeking = false
            applyContentSeek(seekBar)
        }

    }

    private inner class PlayerListener : Player.Listener {

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            applyMetadata(mediaMetadata)
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            applyIsPlaying(isPlaying)
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            updateProgress()
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            applyShuffleMod(shuffleModeEnabled)
        }
    }
}