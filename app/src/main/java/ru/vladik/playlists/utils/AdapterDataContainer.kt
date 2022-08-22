package ru.vladik.playlists.utils

import java.util.*

open class AdapterDataContainer<T>(var data: List<T>) {

    var displayData: List<T> = LinkedList(data)

    fun resetDisplayData() {
        displayData = data
    }
}