package ru.vladik.playlists.utils

import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable

import android.graphics.BitmapFactory

import android.graphics.Bitmap

import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object DrawableUtil {
    fun drawableFromUrl(url: String): Drawable {
        val x: Bitmap
        val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
        connection.connect()
        val input: InputStream = connection.getInputStream()
        x = BitmapFactory.decodeStream(input)
        return BitmapDrawable(Resources.getSystem(), x)
    }

    fun ImageView.setImageAsync(url: String, imMain: OnImageSetListener? = null) {
        var drawable: Drawable? = null
        AsyncUtils.asyncLaunch({
            drawable = drawableFromUrl(url)
        }, {
            this.setImageDrawable(drawable)
            imMain?.onImageSet(drawable)
        })
    }

    interface OnImageSetListener {
        fun onImageSet(drawable: Drawable?)
    }
}