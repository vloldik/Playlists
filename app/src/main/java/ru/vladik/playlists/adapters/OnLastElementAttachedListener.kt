package ru.vladik.playlists.adapters

interface OnLastElementAttachedListener {
    fun onLastViewAttached(position: Int)

    class Default : OnLastElementAttachedListener {

        override fun onLastViewAttached(position: Int) {
        }

    }
}