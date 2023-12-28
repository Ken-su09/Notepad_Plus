package com.suonk.notepad_plus.domain.note.delete_note

import com.suonk.notepad_plus.firebase.notes.FirebaseNotesRepositoryImpl
import javax.inject.Inject

class DeleteNoteByIdUseCase @Inject constructor(
    private val firebaseNotesRepositoryImpl: FirebaseNotesRepositoryImpl,
    private val deleteNoteRepository: DeleteNoteRepository
) {

    suspend fun invoke(id: Long) {
        firebaseNotesRepositoryImpl.getNoteById(id)
        deleteNoteRepository.deleteNoteEntityById(id)
    }
}