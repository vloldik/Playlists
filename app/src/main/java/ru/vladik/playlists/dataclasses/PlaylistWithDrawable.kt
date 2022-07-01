package ru.vladik.playlists.dataclasses

import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import ru.vladik.playlists.utils.ColorUtils

data class PlaylistWithDrawable(var id: String, var count: String, var title: String,
                                var description: String, var photo: Photo?, var service: MusicService,
                                var drawable: Drawable? = null, var averageDrawableColor: Int? = null) {
    fun toPlaylist(): BasicPlaylist {
        return BasicPlaylist(id, count, title, description, photo, service)
    }

    fun getColor() : Int? {
        return if (drawable != null) {
            ColorUtils.getAverageColor(drawable!!.toBitmap())
        } else {
            null
        }
    }
}