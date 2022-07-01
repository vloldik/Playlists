package ru.vladik.playlists.api.vk.models

import java.io.Serializable

data class AddedTracksResponse(var response: MutableList<AddedTrack>, var error: ApiError): Serializable