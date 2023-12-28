package com.suonk.notepad_plus.domain.note.upsert

import com.suonk.notepad_plus.firebase.notes.FirebaseNotesRepository
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpsertNoteUseCase @Inject constructor(
    private val upsertNoteRepository: UpsertNoteRepository,
    private val firebaseNotesRepository: FirebaseNotesRepository
) {
    suspend fun invoke(noteEntity: NoteEntity, userId: String): Long {
        val noteId = upsertNoteRepository.upsertNoteEntity(noteEntity)
        firebaseNotesRepository.addNoteToFirebaseFirestore(noteEntity, userId, noteId)
        return noteId
    }
}