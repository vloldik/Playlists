package ru.vladik.playlists.api.lastfm.models

import java.io.Serializable

data class ArtistPreview(var name: String, var url: String, var image: MutableList<SizedImage>) :
    Serializable