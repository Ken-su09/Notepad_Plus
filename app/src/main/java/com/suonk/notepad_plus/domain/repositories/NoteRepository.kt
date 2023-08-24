package com.suonk.notepad_plus.domain.repositories

import com.suonk.notepad_plus.model.database.data.entities.NoteEntityWithPictures
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun getAllNotesWithPictures() : Flow<List<NoteEntityWithPictures>>
}