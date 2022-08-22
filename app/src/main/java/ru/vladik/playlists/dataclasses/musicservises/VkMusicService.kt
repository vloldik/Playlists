package ru.vladik.playlists.dataclasses.musicservises

import android.content.Context
import android.graphics.Color
import android.util.Log
import ru.vladik.playlists.R
import ru.vladik.playlists.sqlite.PlatformRegistrationSqlHelper

object VkMusicService : MusicService() {
    override val id: Long = 1
    override val name = "Vk music"
    override val imageRes = R.drawable.vk_logo
    override val colorPrimary = Color.parseColor("#FF639CDF")
    override val colorSecondary = colorPrimary
    override val textColor = Color.WHITE

    override fun checkUserRegistration(context: Context) {
        val info = PlatformRegistrationSqlHelper(context).getServiceRegistrationInfo(this)
        Log.d("main", info.toString())
        loggedIn = info?.loggedIn == true
        userName = info?.username
    }
}