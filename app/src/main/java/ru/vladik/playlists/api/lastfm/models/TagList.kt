package ru.vladik.playlists.api.lastfm.models

import java.io.Serializable

data class TagList(var tag: MutableList<Tag>) : Serializable