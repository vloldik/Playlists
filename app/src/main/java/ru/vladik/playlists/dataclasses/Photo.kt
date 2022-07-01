package ru.vladik.playlists.dataclasses
import java.io.Serializable

data class Photo(var small: String, var medium: String, var large: String) : Serializable