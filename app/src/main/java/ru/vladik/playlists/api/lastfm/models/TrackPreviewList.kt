package ru.vladik.playlists.api.lastfm.models

import java.io.Serializable

data class TrackPreviewList(var track: MutableList<TrackPreview>) : Serializable