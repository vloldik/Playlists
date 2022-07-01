package ru.vladik.playlists.api.vk.models

import java.io.Serializable

data class TracksPage(var count: Int, var items: MutableList<Track>): Serializable