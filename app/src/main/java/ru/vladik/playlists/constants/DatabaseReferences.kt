package ru.vladik.playlists.constants

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object DatabaseReferences {

    val appReference: DatabaseReference = FirebaseDatabase.getInstance().reference.root
        .child("activities")

    val allActivitiesReference: DatabaseReference = appReference.child("activities")

    val usersPathReference: DatabaseReference = appReference.child("users")

    fun getUserReference(userId: String): DatabaseReference {
        return usersPathReference.child(userId)
    }

}