package ru.vladik.playlists.api.lastfm.models

import java.io.Serializable

data class ArtistsPreviewList(var artist: MutableList<ArtistPreview>) : Serializable