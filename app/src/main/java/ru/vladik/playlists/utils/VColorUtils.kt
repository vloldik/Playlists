package ru.vladik.playlists.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import kotlin.math.absoluteValue

object VColorUtils {

    fun getAverageColor(bitmap: Bitmap): Int {
        var redBucket = 0
        var greenBucket = 0
        var blueBucket = 0
        var pixelCount = 0

        for (y in 0 until bitmap.height)
        {
            for (x in 0 until bitmap.width)
            {
                val c: Int = bitmap.getPixel(x, y)

                pixelCount++;
                redBucket += Color.red(c)
                greenBucket += Color.green(c)
                blueBucket += Color.blue(c)
            }
        }

        return Color.rgb(redBucket / pixelCount,
            greenBucket / pixelCount,
        blueBucket / pixelCount)
    }

    fun checkColorTooWhite(colorInt: Int): Int {
        var color = colorInt
        var darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        while (darkness < 0.4) {
            darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
            color = color.darken
        }
        return color
    }

    fun isMediumColor(colorInt: Float): Boolean {
        return colorInt < 0.8f && colorInt > 0.2f
    }

    fun getContrastColor(colorInt: Int): Int {
        val color = Color.valueOf(colorInt)
        var r = 1-color.red()
        var g = 1-color.green()
        var b = 1-color.blue()

        while (isMediumColor(r)&& isMediumColor(g)&& isMediumColor(b)) {
            r += 0.1f
            g += 0.1f
            b += 0.1f
        }
        Log.d("main", "r=$r g=$g b=$b")
        return Color.valueOf(r, g, b).toArgb()
    }

    inline val @receiver:ColorInt Int.darken
        @ColorInt
        get() = ColorUtils.blendARGB(this, Color.BLACK, 0.2f)
}