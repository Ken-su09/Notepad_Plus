package com.suonk.notepad_plus.firebase.notes

import com.suonk.notepad_plus.model.database.data.entities.NoteEntity

interface FirebaseNotesRepository {

    fun addNoteToFirebaseFirestore(note: NoteEntity)

    fun getListOfNotesFirebaseFirestoreByUserId(userId: Long)

    fun getNoteById(id: Long)
}