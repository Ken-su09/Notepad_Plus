package com.suonk.notepad_plus.firebase.user

import com.google.firebase.auth.FirebaseAuth
import com.suonk.notepad_plus.model.database.data.entities.CustomFirebaseUser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomFirebaseUserRepository @Inject constructor(private val auth: FirebaseAuth) {

    fun getCustomFirebaseUser(): CustomFirebaseUser {
        return CustomFirebaseUser(
            id = auth.currentUser?.uid,
            name = auth.currentUser?.displayName,
            mail = auth.currentUser?.email,
            photo = auth.currentUser?.photoUrl.toString()
        )
    }
}