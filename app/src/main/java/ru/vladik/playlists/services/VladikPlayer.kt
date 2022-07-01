package ru.vladik.playlists.services

import android.media.MediaPlayer
import ru.vladik.playlists.dataclasses.PlayableTrack

interface VladikPlayer {

    var trackList: ArrayList<PlayableTrack>?
    val currentTrack : PlayableTrack?
    var currentTrackPos : Int
    val tracksCount : Int
    var isShuffle: Boolean
    var isPlayAlways: Boolean

    fun skipToNextTrack()
    fun skipToPreviousTrack()
    fun play()

    interface OnPauseListener {
        fun onPause(pause: Boolean)
    }
    interface OnSleepListener{
        fun onSleep()
    }
}