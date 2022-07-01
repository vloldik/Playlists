package ru.vladik.playlists.api.lastfm

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import ru.vladik.playlists.api.lastfm.models.ArtistSearchPage
import ru.vladik.playlists.api.lastfm.models.SingleArtistPage
import ru.vladik.playlists.api.lastfm.models.TrackSearchPage

interface LastFmService {
    @GET("?method=artist.getinfo&format=json")
    fun fetchArtist(@Query("artist") artistName: String): Call<SingleArtistPage?>

    @GET("?method=artist.search&format=json")
    fun searchArtist(@Query("artist") artistName: String,
                     @Query("page") page: Int? = null,
                     @Query("limit") limit: Int? = null): Call<ArtistSearchPage?>

    @GET("?method=track.search&format=json")
    fun searchTrack(@Query("track") trackName: String,
                    @Query("artist") artistName: String? = null,
                    @Query("playlist_id") playlistId: Long,
                    @Query("page") page: Int? = null,
                    @Query("limit") limit: Int? = null): Call<TrackSearchPage?>
}