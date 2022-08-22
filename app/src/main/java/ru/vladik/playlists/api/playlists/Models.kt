package ru.vladik.playlists.api.playlists

import ru.vladik.playlists.constants.DEFAULT_STRING

data class User(val id: String = DEFAULT_STRING, var mail: String = DEFAULT_STRING,
                var photoUrl: String = DEFAULT_STRING, var name: String = DEFAULT_STRING,
                var description: String = DEFAULT_STRING, var publishedPlaylistsCount: Int = 0)

data class PlaylistPreview(val id: Long = -1, var name: String = DEFAULT_STRING,
                           var description: String = DEFAULT_STRING, val likes: Likes = Likes()
)

data class Playlist(val playlistPreview: PlaylistPreview)

data class Likes(val id: Long = -1, var likesCount: Int = 0, var activeUsersCount: Int = 0,
                 var viewsCount: Int = 0)

data class Track(val id: Long, var name: String)


