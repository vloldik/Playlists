package ru.vladik.playlists.utils

object Extensions {
    fun Number?.minusOneIfNull(): Number {
        return this ?: -1
    }
}