package ru.vladik.playlists.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.vladik.playlists.dataclasses.musicservises.LastFmMusicService
import ru.vladik.playlists.dataclasses.musicservises.MusicService
import ru.vladik.playlists.dataclasses.musicservises.VkMusicService

object AppServices {
    fun getServicesList(): MutableList<MusicService> {
        return arrayListOf(VkMusicService)
    }

}