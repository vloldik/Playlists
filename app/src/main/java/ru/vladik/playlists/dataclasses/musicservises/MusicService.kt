package ru.vladik.playlists.dataclasses.musicservises

import android.content.Context
import java.io.Serializable

abstract class MusicService: Serializable {

    abstract val id: Long
    abstract val name: String
    abstract val imageRes: Int
    abstract val colorPrimary: Int
    abstract val colorSecondary: Int
    abstract val textColor: Int

    var userName: String? = null
    var loggedIn: Boolean = false

    abstract fun checkUserRegistration(context: Context)

    fun setUserInfo(loggedIn: Boolean, userName: String?) {
        this.loggedIn = loggedIn
        this.userName = userName
    }

    override fun equals(other: Any?): Boolean {
        return try {
            (other as MusicService).name == name
        } catch (e: Exception) {
            false
        }
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}