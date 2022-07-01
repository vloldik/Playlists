package ru.vladik.playlists.utils

import android.content.Context
import android.util.Log
import ru.vladik.playlists.R
import ru.vladik.playlists.activities.VkAuthActivity
import ru.vladik.playlists.api.lastfm.LastFmService
import ru.vladik.playlists.api.playlists.Playlist
import ru.vladik.playlists.api.playlists.User
import ru.vladik.playlists.api.vk.models.User as VkUser
import ru.vladik.playlists.api.vk.models.Album as VkAlbum
import ru.vladik.playlists.api.vk.VkService
import ru.vladik.playlists.constants.Strings
import ru.vladik.playlists.dataclasses.MusicService
import ru.vladik.playlists.services.VladikMusicPlayService

object Constants {
    fun getVkUserAudiosAsPlaylist(context: Context, vkUser: VkUser, count: Int) : VkAlbum {
        return VkAlbum(
            -1, context.getString(R.string.userVkPlaylistName), vkUser.id, -1,
            context.getString(R.string.userVkPlaylistDescription),
            count, -1, -1, -1, -1, ArrayList(),
            null, false, "", ""
        )
    }

    lateinit var user: User

    var vkUser: VkUser? = null
        set(user) {
            MusicServicesUtil.notifyUserChanged(AppServices.vk)
            field = user
        }

    lateinit var lastFmService: LastFmService
    var musicPlayService: VladikMusicPlayService? = null
    var vkService: VkService? = null

}

object AppServices {
    var lastFm = MusicService(Strings.LAST_FM_MUSIC_SERVICE_NAME)
    var vk = MusicService(Strings.VK_MUSIC_SERVICE_NAME)

    fun getServicesList(): Array<MusicService> {
        return arrayOf(lastFm, vk)
    }
}
