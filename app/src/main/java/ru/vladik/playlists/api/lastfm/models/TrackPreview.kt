package ru.vladik.playlists.api.lastfm.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TrackPreview(var name: String, var artist: String, var url: String, var streamable: Boolean,
                        var listeners: String, var image: MutableList<SizedImage>,
                        @SerializedName("mbid") var mbId: String) : Serializable