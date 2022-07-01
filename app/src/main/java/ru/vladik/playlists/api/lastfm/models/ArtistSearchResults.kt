package ru.vladik.playlists.api.lastfm.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ArtistSearchResults(@SerializedName("opensearch:Query") var query: SearchQuery,
                               @SerializedName("opensearch:totalResults") var totalResults: String,
                               @SerializedName("opensearch:startIndex") var startIndex: String,
                               @SerializedName("opensearch:itemsPerPage") var itemsPerPage: String,
                               @SerializedName("artistmatches") var artistMatches: ArtistFullPreviewList
) : Serializable