package ru.vladik.playlists.dataclasses.musicservises

import android.content.Context
import android.graphics.Color
import ru.vladik.playlists.R

object LastFmMusicService : MusicService() {
    override val id: Long = 0L
    override val name: String
        get() = "Last fm"
    override val imageRes: Int
        get() = R.drawable.lastfm_logo
    override val colorPrimary: Int
        get() = Color.parseColor("#FF7C0903")
    override val colorSecondary: Int
        get() = colorPrimary
    override val textColor: Int
        get() = Color.WHITE

    override fun checkUserRegistration(context: Context) {
        loggedIn = false
        userName = null
    }
}
