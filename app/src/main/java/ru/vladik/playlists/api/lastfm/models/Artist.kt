package ru.vladik.playlists.api.lastfm.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Artist(var name: String, @SerializedName("mbid") var mbId: String,
                  var url: String, var streamable: Boolean,
                  @SerializedName("outour") var outOur: Boolean,
                  var image: MutableList<SizedImage>, var stats: ArtistStats,
                  var similar: ArtistsPreviewList,
                  var tags: TagList, var bio: ArtistBio
) : Serializable