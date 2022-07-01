package ru.vladik.playlists.api.vk.models

import java.io.Serializable

data class TracksResponse(var response: TracksPage, var error: ApiError?): Serializable

