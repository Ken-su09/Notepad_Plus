package com.suonk.notepad_plus.domain.use_cases.note.id

import com.suonk.notepad_plus.domain.repositories.CurrentNoteIdRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCurrentIdFlowChannelUseCase @Inject constructor(private val currentNoteIdRepository: CurrentNoteIdRepository) {

    fun invoke(): Flow<Long?> {
        return currentNoteIdRepository.getCurrentNoteIdChannel().receiveAsFlow()
    }
}