package ru.vladik.playlists.api.vk.models

data class TrackListResponse(var response: MutableList<Track>, var error: ApiError?)