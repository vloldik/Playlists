package ru.vladik.playlists.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.vladik.playlists.R
import ru.vladik.playlists.adapters.recycler_view.EmptyAdapter
import ru.vladik.playlists.adapters.recycler_view.PlaylistsAdapter
import ru.vladik.playlists.api.vk.models.Album
import ru.vladik.playlists.api.vk.models.AlbumsPage
import ru.vladik.playlists.dataclasses.MusicService
import ru.vladik.playlists.extensions.ApiExtensions.toPlaylistList
import ru.vladik.playlists.utils.*

class playlist_list_fragment : Fragment() {

    private val SERVICE = "service"
    private lateinit var playlistLogo: Drawable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_playlist_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val service = arguments?.getSerializable(SERVICE) as MusicService
        setService(service)
    }

    private fun setService(service: MusicService) {
        if (view != null && context!=null) {
            playlistLogo = AppCompatResources.getDrawable(context!!, R.drawable.playlist)!!

            val playlistRecyclerView = view!!.findViewById<RecyclerView>(R.id.playlist_recycler_view)
            playlistRecyclerView.layoutManager = LinearLayoutManager(context)
            playlistRecyclerView.adapter = EmptyAdapter()

            when(service) {
                AppServices.vk -> {
                    playlistRecyclerView.adapter = PlaylistsAdapter(context!!)
                    val vkService = Constants.vkService
                    val vkUser = Constants.vkUser
                    lateinit var albumsPage: AlbumsPage
                    if (vkService != null && vkUser != null) {
                        LayoutHelper.setLoading(view as ViewGroup)
                        AsyncUtils.asyncLaunch({
                            albumsPage = vkService.getAlbums(vkUser.id).execute().body().response
                            val userTrackCount = vkService.getTracks(vkUser.id).execute().body().response.count
                            albumsPage.items.add(0, Constants.getVkUserAudiosAsPlaylist(
                                context!!, vkUser, userTrackCount))
                        }, {
                            LayoutHelper.setLoading(view as ViewGroup, false)
                            (playlistRecyclerView.adapter as PlaylistsAdapter).loadData(albumsPage.toPlaylistList())
                        })
                    }
                }
            }
        }
    }

    companion object {
        fun getInstance(service: MusicService): playlist_list_fragment {
            val fragment = playlist_list_fragment()
            fragment.apply {
                arguments = bundleOf(SERVICE to service)
            }
            return fragment
        }
    }
}