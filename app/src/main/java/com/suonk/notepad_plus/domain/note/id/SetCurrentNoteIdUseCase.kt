package com.suonk.notepad_plus.domain.note.id

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetCurrentNoteIdUseCase @Inject constructor(private val currentNoteIdRepository: CurrentNoteIdRepository) {

    fun invoke(id: Long) {
        currentNoteIdRepository.setCurrentNoteIdFlow(id)
    }
}