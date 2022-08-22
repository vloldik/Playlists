package ru.vladik.playlists.api.vk.models

import android.util.Log
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(var id: Long,
                @SerializedName("first_name") var firstName: String,
                @SerializedName("last_name") var lastName: String,
                @SerializedName("can_access_closed") var canAccessClosed: Boolean,
                @SerializedName("is_closed") var isClosed: Boolean): Serializable {

    constructor(set: Set<String>) : this(
        set.toTypedArray()[0].toLong(),
        set.toTypedArray()[1],
        set.toTypedArray()[2],
        set.toTypedArray()[3].toBooleanStrict(),
        set.toTypedArray()[4].toBooleanStrict(),
    )

    fun toStringSet() : Set<String> {
        val set = LinkedHashSet<String>()
        return set.apply {
            add(id.toString())
            add(firstName)
            add(lastName)
            add(canAccessClosed.toString())
            add(isClosed.toString())
            Log.d("main", this.toString())
        }
    }
}