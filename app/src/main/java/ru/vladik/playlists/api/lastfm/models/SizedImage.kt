package ru.vladik.playlists.api.lastfm.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SizedImage(@SerializedName("#text") val text: String, val size: String) : Serializable