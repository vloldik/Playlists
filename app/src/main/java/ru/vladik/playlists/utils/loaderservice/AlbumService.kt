package ru.vladik.playlists.utils.loaderservice

import android.content.Context
import android.util.Log
import ru.vladik.playlists.dataclasses.albums.AlbumModel
import ru.vladik.playlists.utils.loaders.AlbumLoader
import ru.vladik.playlists.utils.loaders.AlbumSearcher

class AlbumService(private var context: Context, platformId: Long) {
    var platformId = platformId
        set(value) {
            field = value
            resetLoaders()
            load()
        }

    var callback: ((MutableList<out AlbumModel>) -> Unit)? = null
        set(value) {
            field = value
            searcher.callback = callback
            loader.callback = callback
        }
    var searchQuery = ""
        set(value) {
            field = value
            searcher.q = searchQuery

            resetPages()
            load()
        }

    private var searcher: AlbumSearcher
    private var loader: AlbumLoader

    init {
        searcher =  AlbumSearcher.getAlbumSearcherForPlatform(context, platformId)
        loader = AlbumLoader.getAlbumLoaderForPlatform(context, platformId)
    }

    private fun resetLoaders() {
        searcher =  AlbumSearcher.getAlbumSearcherForPlatform(context, platformId)
        loader = AlbumLoader.getAlbumLoaderForPlatform(context, platformId)
    }

    fun resetPages() {
        searcher.currentPage = 0
        loader.currentPage = 0
    }

    fun load() {
        Log.d("main", searchQuery)
        if (searchQuery.isEmpty()) {
            loader.nextPage()
            return
        }
        searcher.nextPage()
    }
}