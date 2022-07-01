package ru.vladik.playlists.dataclasses

import android.graphics.Color
import androidx.annotation.DrawableRes
import java.io.Serializable

data class MusicService(
    var name: String, var loggedIn: Boolean = false, @DrawableRes var image: Int? = null,
    var colorPrimary: Int = Color.BLACK, val colorSecondary: Int = colorPrimary,
    var textColor: Int = Color.WHITE, var userName: String? = null,
) : Serializable