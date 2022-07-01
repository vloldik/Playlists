package ru.vladik.playlists.api.lastfm.models

import java.io.Serializable

data class ArtistBio(var links: LinkSingle, var published: String, var summary: String, var content: String) :
    Serializable