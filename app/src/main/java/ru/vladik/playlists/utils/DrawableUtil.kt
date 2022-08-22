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


    interface OnImageSetListener {
        fun onImageSet(drawable: Drawable?)
    }
}