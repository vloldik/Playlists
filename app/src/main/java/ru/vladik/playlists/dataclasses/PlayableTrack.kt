package ru.vladik.playlists.dataclasses

import ru.vladik.playlists.utils.AppServices

data class PlayableTrack(override var tId: String,
                         override var tName: String,
                         override var tAuthor: String,
                         override var tDuration: Int,
                         private var photo: Photo?,
                         var url: String) :
    TrackModel(tId, tName, tAuthor, tDuration) {
    override fun getService(): MusicService {
        return AppServices.lastFm
    }

    override fun getPhoto(): Photo? {
        return photo
    }
}