package ru.vladik.playlists.api.lastfm.models

import java.io.Serializable

data class ArtistFullPreviewList(var artist: MutableList<ArtistFullPreview>) : Serializable