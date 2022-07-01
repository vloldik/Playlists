package ru.vladik.playlists.api.lastfm.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SearchQuery(@SerializedName("#text") val text: String, var role: String,
                       var searchTerms: String, @SerializedName("startPage") var page: String) :
    Serializable