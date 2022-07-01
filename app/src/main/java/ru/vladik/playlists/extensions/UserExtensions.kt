package ru.vladik.playlists.extensions

import ru.vladik.playlists.api.playlists.User
import ru.vladik.playlists.extensions.StringExtension.isDefault

object UserExtensions {

    fun User.exists(): Boolean {
        return !this.id.isDefault()
    }

}