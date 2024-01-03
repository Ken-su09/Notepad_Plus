package com.suonk.notepad_plus.firebase.notes

import android.util.Log
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseNotesRepositoryImpl @Inject constructor(private val firebaseFirestore: FirebaseFirestore) : FirebaseNotesRepository {

    override fun addNoteToFirebaseFirestore(note: NoteEntity, userId: String, noteId: Long) {
        firebaseFirestore.collection(ALL_USERS).whereEqualTo(ID, userId).get().addOnCompleteListener { task ->
            if (task.result.isEmpty) {
                firebaseFirestore.collection(ALL_USERS).document(userId).collection(ALL_NOTES).document(noteId.toString()).set(note)
            }
        }
    }

    override fun getListOfNotesFirebaseFirestoreByUserId(userId: String): Flow<List<NoteEntity>> = callbackFlow {
        val listenerRegistration =
            firebaseFirestore.collection(ALL_USERS).document(userId).collection(ALL_NOTES).addSnapshotListener { querySnapshot, error ->
                if (querySnapshot != null) {
                    try {
                        trySend(querySnapshot.toObjects(NoteEntity::class.java))
                    } catch (e: Exception) {
                        Log.i("FirebaseError", "Exception : $e")
                    }
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    override fun getNoteById(userId: String, noteId: Long): Flow<NoteEntity> = callbackFlow {
        val listenerRegistration = firebaseFirestore.collection(ALL_USERS).document(userId).collection(ALL_NOTES).document(noteId.toString())
            .addSnapshotListener { querySnapshot, error ->
                if (querySnapshot != null) {
                    try {
                        querySnapshot.toObject(NoteEntity::class.java)?.let { trySend(it) }
                    } catch (e: Exception) {
                        Log.i("FirebaseError", "Exception : $e")
                    }
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    override fun deleteNoteById(userId: String, noteId: Long) {
        firebaseFirestore.collection(ALL_USERS).document(userId).collection(ALL_NOTES).document(noteId.toString()).delete()
    }

    companion object {
        private const val ALL_USERS = "ALL_USERS"
        private const val ALL_NOTES = "ALL_NOTES"
        private const val ID = "ID"
    }
}