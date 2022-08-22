package ru.vladik.playlists.constants

import ru.vladik.playlists.dataclasses.Photo
import ru.vladik.playlists.dataclasses.albums.AlbumModel
import ru.vladik.playlists.dataclasses.musicservises.VkMusicService

object UserAudiosAsPlaylist : AlbumModel() {
    override var id = ""
    override var title = "Ваши аудио"
    override var description = "Автоматически созданный плейлист"
    override var count = -1
    override val photo: Photo? = null
    override val service = VkMusicService
    override val isEnabled = true
    override var ownerId: String = ""
}

