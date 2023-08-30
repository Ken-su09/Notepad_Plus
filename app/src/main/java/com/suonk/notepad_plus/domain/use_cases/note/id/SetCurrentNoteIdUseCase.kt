package com.suonk.notepad_plus.domain.use_cases.note.id

import com.suonk.notepad_plus.domain.repositories.CurrentNoteIdRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetCurrentNoteIdUseCase @Inject constructor(private val currentNoteIdRepository: CurrentNoteIdRepository) {

    fun invoke(id: Long) {
        currentNoteIdRepository.setCurrentNoteIdFlow(id)
    }
}