package com.suonk.notepad_plus.domain.note.delete_note

import com.suonk.notepad_plus.model.database.data.dao.NoteDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteNoteRepositoryImpl @Inject constructor(private val noteDao: NoteDao) : DeleteNoteRepository {
    override suspend fun deleteNoteEntityById(id: Long) {
        noteDao.deleteNoteEntityById(id)
    }
}