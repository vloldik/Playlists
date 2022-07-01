package ru.vladik.playlists.dataclasses

import ru.vladik.playlists.utils.AppServices
import java.io.Serializable

data class BasicTrack(override var tId: String, override var tName: String,
                      override var tAuthor: String, override var tDuration: Int,
                      private var photo: Photo?) : TrackModel(tId, tName, tAuthor, tDuration), Serializable {
    override fun getService(): MusicService {
        return AppServices.lastFm
    }

    override fun getPhoto(): Photo? {
        return photo
    }
}