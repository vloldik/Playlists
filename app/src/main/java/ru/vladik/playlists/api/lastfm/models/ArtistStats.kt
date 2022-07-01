package ru.vladik.playlists.api.lastfm.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ArtistStats(var listeners: String, @SerializedName("playcount") var playCount: String) :
    Serializable