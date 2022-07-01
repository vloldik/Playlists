package ru.vladik.playlists.utils

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import ru.vladik.playlists.constants.DatabaseReferences
import ru.vladik.playlists.api.playlists.User

object FirebaseUtil {

    fun getUser(userId: String): Task<DataSnapshot> {
        return DatabaseReferences.getUserReference(userId).get()
    }

    fun setUser(user: User) {
        DatabaseReferences.getUserReference(user.id).setValue(user)
    }

}