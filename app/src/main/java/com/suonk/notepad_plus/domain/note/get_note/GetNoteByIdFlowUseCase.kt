package com.suonk.notepad_plus.domain.note.get_note

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetNoteByIdFlowUseCase @Inject constructor(private val getNotesRepository: GetNotesRepository) {

    fun invoke(id: Long) = getNotesRepository.getNoteById(id)
}