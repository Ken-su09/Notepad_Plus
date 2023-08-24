package com.suonk.notepad_plus.firebase.user

import com.suonk.notepad_plus.model.database.data.entities.CustomFirebaseUser

interface UsersRepository {

    fun addNewUserToFirestore(id: String, customFirebaseUser: CustomFirebaseUser)
}