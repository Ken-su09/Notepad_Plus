package com.suonk.notepad_plus.domain.use_cases.note.get_note

import com.suonk.notepad_plus.domain.repositories.NoteRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetNoteByIdFlowUseCase @Inject constructor(private val noteRepository: NoteRepository) {

    fun invoke(id: Long) = noteRepository.getNoteById(id)
}