package ru.vladik.playlists.services

import android.media.MediaPlayer
import android.util.Log
import android.util.TimeUtils
import ru.vladik.playlists.dataclasses.PlayableTrack
import java.sql.Time
import java.util.concurrent.TimeUnit

class VladikMediaPlayer : VladikPlayer, MediaPlayer() {
    private var mTrackList: ArrayList<PlayableTrack>? = null
    private var trackTimePosition = 0
    override var trackList: ArrayList<PlayableTrack>? = null
        set(value) {
            mTrackList = if (!isShuffle) value else value?.shuffled() as ArrayList<PlayableTrack>?
            field = value
        }
    override var currentTrack: PlayableTrack?
        get() {
            return if (mTrackList!= null && currentTrackPos < mTrackList!!.size)
                mTrackList?.get(currentTrackPos)
            else null
        }
        set(value) {
            if (mTrackList != null) currentTrackPos = mTrackList!!.indexOf(value)
        }
    override var currentTrackPos: Int = 0
    override val tracksCount: Int
        get() {
            return if (trackList != null) {
                trackList!!.size
            } else {
                0
            }
        }
    override var isShuffle: Boolean = false
        set(value) {
            mTrackList = if (value) {
                trackList?.shuffled() as ArrayList<PlayableTrack>?
            } else {
                trackList
            }
            if (mTrackList != null && currentTrack != null) {
                currentTrackPos = mTrackList!!.indexOf(currentTrack)
            }
            field = value
        }
    override var isPlayAlways: Boolean = false
    var onPauseListener: VladikPlayer.OnPauseListener? = null
    var onSleepListener: VladikPlayer.OnSleepListener? = null

    init {
        setVolume(1.0f, 1.0f)
    }

    override fun pause() {
        if (isPlaying) {
            trackTimePosition = currentPosition
            super.pause()
            onPauseListener?.onPause(true)
        } else {
            seekTo(trackTimePosition)
            super.start()
            onPauseListener?.onPause(false)
        }
    }

    override fun skipToNextTrack() {
        currentTrackPos++

        if (mTrackList != null && currentTrackPos >= mTrackList!!.size && isPlayAlways) {
            currentTrackPos = 0
        }
        play()
    }

    override fun skipToPreviousTrack() {
        if (currentPosition < TimeUnit.MILLISECONDS.convert(10, TimeUnit.SECONDS) && currentTrackPos > 0) {
            currentTrackPos--
        } else {
            seekTo(0)
        }
        play()
    }

    override fun play() {
        reset()
        if (currentTrack != null) {
            setDataSource(currentTrack?.url)
            prepareAsync()
        }
    }

    fun sleep() {
        stop()
        onSleepListener?.onSleep()
    }
}