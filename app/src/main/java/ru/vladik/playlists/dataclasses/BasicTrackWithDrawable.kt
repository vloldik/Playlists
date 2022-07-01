package ru.vladik.playlists.dataclasses

import android.graphics.drawable.Drawable

data class BasicTrackWithDrawable(override var tId: String, override var tName: String,
                                  override var tAuthor: String, override var tDuration: Int,
                                  private var photo: Photo?,
                                  var drawable: Drawable? = null):
    TrackModel(tId, tName, tAuthor, tDuration) {

    override fun getService(): MusicService {
        TODO("Not yet implemented")
    }

    override fun getPhoto(): Photo? {
        return this.photo
    }
}