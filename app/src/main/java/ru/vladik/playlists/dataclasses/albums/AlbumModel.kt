package ru.vladik.playlists.dataclasses.albums

import ru.vladik.playlists.dataclasses.Photo
import ru.vladik.playlists.dataclasses.musicservises.MusicService
import java.io.Serializable

abstract class AlbumModel : Serializable{
    abstract var id: String
    abstract var ownerId: String
    abstract var title: String
    abstract var description: String
    abstract var count: Int

    abstract val isEnabled: Boolean
    abstract val photo: Photo?
    abstract val service: MusicService
}