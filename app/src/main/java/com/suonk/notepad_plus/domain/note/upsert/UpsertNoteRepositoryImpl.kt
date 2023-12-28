package com.suonk.notepad_plus.domain.note.upsert

import com.suonk.notepad_plus.model.database.data.dao.NoteDao
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpsertNoteRepositoryImpl @Inject constructor(private val dao: NoteDao) : UpsertNoteRepository {

    override suspend fun upsertNoteEntity(noteEntity: NoteEntity): Long = dao.upsertNoteEntity(noteEntity)
}