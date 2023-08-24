package com.suonk.notepad_plus.domain.repositories

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow

interface CurrentNoteIdRepository {

    fun getCurrentNoteIdFlow(): StateFlow<Long?>
    fun getCurrentNoteIdChannel(): Channel<Long?>

    fun setCurrentNoteIdFlow(id: Long?)
}