package com.suonk.notepad_plus.firebase.user

import com.google.firebase.firestore.FirebaseFirestore
import com.suonk.notepad_plus.model.database.data.entities.CustomFirebaseUser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseUsersRepositoryImpl @Inject constructor(private val firebaseFirestore: FirebaseFirestore) : FirebaseUsersRepository {

    override fun addNewUserToFirestore(id: String, customFirebaseUser: CustomFirebaseUser) {
        firebaseFirestore.collection(ALL_USERS)
            .whereEqualTo(ID, id)
            .get()
            .addOnCompleteListener { task ->
                if (task.result.isEmpty){
                    firebaseFirestore.collection(ALL_USERS).document(id).set(customFirebaseUser)
                }
            }
    }

    companion object {
        private const val ALL_USERS = "ALL_USERS"
        private const val ID = "ID"
    }
}