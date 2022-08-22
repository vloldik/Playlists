package ru.vladik.playlists.utils.loaders

import android.content.Context
import ru.vladik.playlists.dataclasses.albums.AlbumModel
import ru.vladik.playlists.dataclasses.musicservises.LastFmMusicService
import ru.vladik.playlists.dataclasses.musicservises.VkMusicService
import java.lang.IllegalStateException

interface AlbumLoader {

    var currentPage: Int
    var count: Int

    var isLoading: Boolean
    var hasNext: Boolean

    var callback: ((MutableList<out AlbumModel>) -> Unit)?

    fun nextPage()
    fun prevPage()

    fun loadPage(page: Int, onResponse: (MutableList<out AlbumModel>) -> Unit)

    companion object {
        fun getAlbumLoaderForPlatform(context: Context, platformId: Long): AlbumLoader {
            if (platformId == VkMusicService.id) {
                return VkAlbumLoader(context)
            }
            throw IllegalStateException("Platform id cannot be $platformId")
        }
        const val DEFAULT_COUNT = 0
    }
}