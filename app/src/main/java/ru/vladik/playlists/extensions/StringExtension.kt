package ru.vladik.playlists.extensions

import ru.vladik.playlists.constants.Strings

object StringExtension {

    fun String.isDefault(): Boolean {
        return this == Strings.DEFAULT_STRING
    }
}