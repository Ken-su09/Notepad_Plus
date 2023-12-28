package com.suonk.notepad_plus.firebase.notes

import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import kotlinx.coroutines.flow.Flow

interface FirebaseNotesRepository {

    fun addNoteToFirebaseFirestore(note: NoteEntity, userId: String)

    fun getListOfNotesFirebaseFirestoreByUserId(userId: String): Flow<List<NoteEntity>>

    fun getNoteById(id: Long)

    fun deleteNoteById(id: Long)
}