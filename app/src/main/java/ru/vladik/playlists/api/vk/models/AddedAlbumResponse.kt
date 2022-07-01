package ru.vladik.playlists.api.vk.models

import java.io.Serializable

data class AddedAlbumResponse(var response: Album, var error: ApiError): Serializable