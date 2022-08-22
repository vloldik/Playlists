package ru.vladik.playlists.dataclasses

import android.os.Parcel
import android.os.Parcelable
import ru.vladik.playlists.dataclasses.musicservises.MusicService
import ru.vladik.playlists.utils.AppServices

data class MusicSourceInfo(val service: MusicService, val playlistId: String, var count: Int = 3) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        AppServices.getServicesList()[parcel.readInt()],
        parcel.readString()!!,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(AppServices.getServicesList().indexOf(service))
        parcel.writeString(playlistId)
        parcel.writeInt(count)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR : Parcelable.Creator<MusicSourceInfo> = object : Parcelable.Creator<MusicSourceInfo> {
            override fun createFromParcel(parcel: Parcel): MusicSourceInfo {
                return MusicSourceInfo(parcel)
            }

            override fun newArray(size: Int): Array<MusicSourceInfo?> {
                return arrayOfNulls(size)
            }
        }
    }

    fun needsToReplace(other: MusicSourceInfo?) : Boolean {
        return (other != null && !(other.playlistId == playlistId || other.service == service || other.count < count))
    }

}