package ru.vladik.playlists.api.lastfm.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ArtistFullPreview(var name: String, @SerializedName("mbid") var mbId: String,
                             var listeners: String, var url: String, var streamable: Boolean,
                             var image: MutableList<SizedImage>) : Serializable