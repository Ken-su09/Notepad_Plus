package com.suonk.notepad_plus.firebase.user

import com.suonk.notepad_plus.model.database.data.entities.CustomFirebaseUser

interface FirebaseUsersRepository {

    fun addNewUserToFirestore(id: String, customFirebaseUser: CustomFirebaseUser)
}