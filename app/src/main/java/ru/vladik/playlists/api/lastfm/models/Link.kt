package ru.vladik.playlists.api.lastfm.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Link(@SerializedName("#text") var text: String, var rel: String, var href: String) :
    Serializable