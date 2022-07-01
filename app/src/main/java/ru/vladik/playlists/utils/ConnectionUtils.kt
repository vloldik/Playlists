package ru.vladik.playlists.utils

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import java.net.URL

object ConnectionUtils {

    fun drawableFromUrl(url: String): BitmapDrawable {
        val connection = URL(url).openConnection()
        connection.connect()
        val inputStream = connection.getInputStream()

        val x = BitmapFactory.decodeStream(inputStream)
        return BitmapDrawable(Resources.getSystem(), x)
    }

}