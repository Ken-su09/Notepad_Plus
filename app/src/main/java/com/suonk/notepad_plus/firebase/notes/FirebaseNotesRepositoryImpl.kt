package com.suonk.notepad_plus.firebase.notes

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.suonk.notepad_plus.firebase.user.FirebaseUsersRepositoryImpl
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseNotesRepositoryImpl @Inject constructor(private val firebaseFirestore: FirebaseFirestore) : FirebaseNotesRepository {

    override fun addNoteToFirebaseFirestore(note: NoteEntity, userId: String) {
        firebaseFirestore.collection(ALL_USERS)
            .whereEqualTo(ID, userId)
            .get()
            .addOnCompleteListener { task ->
                if (task.result.isEmpty) {
                    firebaseFirestore.collection(ALL_USERS).document(userId).firestore.collection(ALL_NOTES).document(note.id.toString()).set(note)
                }
            }
    }

    override fun getListOfNotesFirebaseFirestoreByUserId(userId: String): Flow<List<NoteEntity>> {
        val allNotesStateFlow = MutableStateFlow<List<NoteEntity>>(emptyList())

        firebaseFirestore.collection(ALL_USERS).document(userId).firestore.collection(ALL_NOTES)
            .addSnapshotListener { querySnapshot, error ->
                if (querySnapshot != null) {
                    try {
                        allNotesStateFlow.value = querySnapshot.toObjects(NoteEntity::class.java)
                    } catch (e: Exception) {
                        Log.i("FirebaseError", "Exception : $e")
                    }
                }
            }

        return allNotesStateFlow.asStateFlow()
    }

    override fun getNoteById(id: Long) {
    }

    override fun deleteNoteById(id: Long) {

    }

    companion object {
        private const val ALL_USERS = "ALL_USERS"
        private const val ALL_NOTES = "ALL_NOTES"
        private const val ID = "ID"
    }
}