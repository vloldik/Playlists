package ru.vladik.playlists.api.vk.models

import java.io.Serializable

data class AlbumsResponse(var response: AlbumsPage, var error: ApiError?): Serializable