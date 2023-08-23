package com.suonk.mynotepad.model.database.data.repositories

import com.suonk.mynotepad.domain.repositories.CurrentNoteIdRepository
import com.suonk.mynotepad.model.database.dao.NoteDao
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentNoteIdRepositoryImpl @Inject constructor(private val dao: NoteDao) : CurrentNoteIdRepository {

    private val currentNoteIdFlow = MutableStateFlow<Long?>(null)
    private val currentNoteIdChannel = Channel<Long?>(
        capacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun getCurrentNoteIdFlow(): StateFlow<Long?> = currentNoteIdFlow

    override fun getCurrentNoteIdChannel(): Channel<Long?>  = currentNoteIdChannel

    override fun setCurrentNoteIdFlow(id: Long?) {
        currentNoteIdFlow.value = id
        currentNoteIdChannel.trySend(id)
    }
}