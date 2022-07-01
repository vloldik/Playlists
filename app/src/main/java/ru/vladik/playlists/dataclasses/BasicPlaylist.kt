package ru.vladik.playlists.dataclasses

import java.io.Serializable

data class BasicPlaylist(var id: String, var count: String, var title: String, var description: String, var photo: Photo?,
                         var service: MusicService)
    : Serializable {
    fun toPlaylistWithDrawable(): PlaylistWithDrawable {
        return PlaylistWithDrawable(id, count, title, description, photo, service)
    }
}