package ru.vladik.playlists.dataclasses

abstract class TrackModel(open var tId: String, open var tName: String,
                          open var tAuthor: String, open var tDuration: Int) {
    fun toTrackWithDrawable(): BasicTrackWithDrawable {
        return BasicTrackWithDrawable(tId, tName, tAuthor, tDuration, getPhoto(), null)
    }

    abstract fun getService(): MusicService
    abstract fun getPhoto(): Photo?
}