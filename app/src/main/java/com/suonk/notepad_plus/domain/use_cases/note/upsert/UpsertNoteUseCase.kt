package com.suonk.notepad_plus.domain.use_cases.note.upsert

import com.suonk.notepad_plus.domain.repositories.NoteRepository
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpsertNoteUseCase @Inject constructor(private val noteRepository: NoteRepository) {

    suspend fun invoke(noteEntity: NoteEntity): Long = noteRepository.upsertNoteEntity(noteEntity)
}