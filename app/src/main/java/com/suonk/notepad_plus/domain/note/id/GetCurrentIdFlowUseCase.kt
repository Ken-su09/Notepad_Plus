package com.suonk.notepad_plus.domain.note.id

import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCurrentIdFlowUseCase @Inject constructor(private val currentNoteIdRepository: CurrentNoteIdRepository) {

    fun invoke(): StateFlow<Long?> {
        return currentNoteIdRepository.getCurrentNoteIdFlow()
    }
}