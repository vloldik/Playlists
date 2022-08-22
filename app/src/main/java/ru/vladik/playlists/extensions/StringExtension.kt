package ru.vladik.playlists.extensions

import ru.vladik.playlists.constants.DEFAULT_STRING

object StringExtension {

    fun String.isDefault(): Boolean {
        return this == DEFAULT_STRING
    }
}