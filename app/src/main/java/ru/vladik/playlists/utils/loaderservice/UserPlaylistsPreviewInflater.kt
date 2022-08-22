package ru.vladik.playlists.utils.loaderservice

import android.content.Intent
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.vladik.playlists.adapters.recycler_view.PlaylistsAdapter
import ru.vladik.playlists.constants.INTENT_PLATFORM
import ru.vladik.playlists.dataclasses.musicservises.MusicService
import ru.vladik.playlists.activities.UsersPlaylistsActivity
import ru.vladik.playlists.utils.AdapterDataContainer
import ru.vladik.playlists.utils.AppServices
import ru.vladik.playlists.utils.loaders.AlbumLoader
import ru.vladik.playlists.views.ListPreviewView

object UserPlaylistsPreviewInflater {
    fun inflate(viewGroup: ViewGroup) {
        val platformList = AppServices.getServicesList()
        platformList.forEach { musicService ->
            addService(musicService, viewGroup)
        }
    }

    private fun addService(musicService: MusicService, viewGroup: ViewGroup) {
        val context = viewGroup.context
        val previewView = ListPreviewView(context)
        viewGroup.addView(previewView)
        previewView.textView.text = musicService.name
        previewView.recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        val adapter = PlaylistsAdapter(AdapterDataContainer(ArrayList()), compact = true)
        previewView.recyclerView.adapter = adapter
        previewView.setOnHeaderClickListener {
            val i = Intent(context, UsersPlaylistsActivity::class.java)
            i.putExtra(INTENT_PLATFORM, musicService.id)
            context.startActivity(i)
        }

        val loader = AlbumLoader.getAlbumLoaderForPlatform(context, musicService.id)
        loader.count = 8
        loader.callback = { playlists ->
            adapter.dataContainer.data += playlists
            adapter.dataContainer.displayData += playlists

            adapter.notifyItemRangeInserted(0, playlists.size)
            previewView.setLoading(false)
        }
        loader.nextPage()
        previewView.setLoading(true)
    }
}
