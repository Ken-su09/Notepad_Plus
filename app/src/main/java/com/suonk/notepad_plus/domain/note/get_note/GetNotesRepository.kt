package com.suonk.notepad_plus.domain.note.get_note

import com.suonk.notepad_plus.model.database.data.entities.NoteEntityWithPictures
import kotlinx.coroutines.flow.Flow

interface GetNotesRepository {

    fun getAllNotesWithPictures(): Flow<List<NoteEntityWithPictures>>

    fun getAllDeletedNotesWithPictures(): Flow<List<NoteEntityWithPictures>>

    fun getNoteById(id: Long): Flow<NoteEntityWithPictures>
}