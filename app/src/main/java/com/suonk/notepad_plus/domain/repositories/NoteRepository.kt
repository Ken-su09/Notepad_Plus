package com.suonk.mynotepad.domain.repositories

import com.suonk.mynotepad.model.database.data.entities.NoteEntityWithPictures
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun getAllNotesWithPictures() : Flow<List<NoteEntityWithPictures>>
}