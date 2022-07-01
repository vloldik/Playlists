package ru.vladik.playlists.api.vk.models

import java.io.Serializable

data class AlbumsPage(var count: Int, var items: MutableList<Album>): Serializable