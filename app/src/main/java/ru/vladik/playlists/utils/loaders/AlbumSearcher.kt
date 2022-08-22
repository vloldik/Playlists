package ru.vladik.playlists.utils.loaders

import android.content.Context
import ru.vladik.playlists.dataclasses.albums.AlbumModel
import ru.vladik.playlists.dataclasses.musicservises.LastFmMusicService
import ru.vladik.playlists.dataclasses.musicservises.VkMusicService
import java.lang.IllegalStateException

interface AlbumSearcher : AlbumLoader {
    var q: String


    companion object {
        fun getAlbumSearcherForPlatform(context: Context, platformId: Long): AlbumSearcher {
            if (platformId == VkMusicService.id) {
                return VkAlbumSearcher(context)
            }
            throw IllegalStateException("Platform id cannot be $platformId")
        }
    }
}