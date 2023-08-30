package com.suonk.notepad_plus.domain.use_cases.note.id

import com.suonk.notepad_plus.domain.repositories.CurrentNoteIdRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCurrentIdFlowUseCase @Inject constructor(private val currentNoteIdRepository: CurrentNoteIdRepository) {

    fun invoke(): StateFlow<Long?> {
        return currentNoteIdRepository.getCurrentNoteIdFlow()
    }
}