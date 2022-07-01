package ru.vladik.playlists.utils

import android.content.Context
import ru.vladik.playlists.R

object StringUtil {
    fun getTrackStringByNum(context: Context, int: String): String {
        when {
            int.endsWith("11") -> return context.getString(R.string.five_six_etc)
            int.endsWith("12") -> return context.getString(R.string.five_six_etc)
            int.endsWith("13") -> return context.getString(R.string.five_six_etc)
            int.endsWith("14") -> return context.getString(R.string.five_six_etc)
            int.endsWith("1") -> return context.getString(R.string.one_track)
            int.endsWith("0") -> return context.getString(R.string.five_six_etc)
            int.endsWith("2") -> return context.getString(R.string.two_three_etc)
            int.endsWith("3") -> return context.getString(R.string.two_three_etc)
            int.endsWith("4") -> return context.getString(R.string.two_three_etc)
            int.endsWith("5") -> return context.getString(R.string.five_six_etc)
            int.endsWith("6") -> return context.getString(R.string.five_six_etc)
            int.endsWith("7") -> return context.getString(R.string.five_six_etc)
            int.endsWith("8") -> return context.getString(R.string.five_six_etc)
            int.endsWith("9") -> return context.getString(R.string.five_six_etc)
            else -> return context.getString(R.string.five_six_etc)
        }
    }

    fun getDurationFromSeconds(secondsFull: Int): String {
        val hours = (secondsFull/3600).toInt()
        val minutes = ((secondsFull - hours*3600)/60).toInt()
        val seconds = secondsFull - hours*3600 - minutes*60
        return when {
            hours != 0 -> {
                "${hours}:${normalizeIntString(minutes, 2)}:${normalizeIntString(seconds, 2)}"
            }

            else-> {
                "${minutes}:${normalizeIntString(seconds, 2)}"
            }
        }
    }

    fun normalizeIntString(int: Int, charCount: Int) : String {
        var string = int.toString()
        if (string.length < charCount) {
            string = "0" * (charCount - string.length - 1) + string
        }
        return string
    }
}

private operator fun String.times(i: Int): String {
    val stringBuilder = StringBuilder()
    for (j in 0..i) {
        stringBuilder.append(this)
    }
    return stringBuilder.toString()
}
