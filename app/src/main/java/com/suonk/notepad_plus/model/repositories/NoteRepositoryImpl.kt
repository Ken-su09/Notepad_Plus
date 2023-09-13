package com.suonk.notepad_plus.model.repositories

import com.suonk.notepad_plus.domain.repositories.NoteRepository
import com.suonk.notepad_plus.model.database.data.dao.NoteDao
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import com.suonk.notepad_plus.model.database.data.entities.NoteEntityWithPictures
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl @Inject constructor(private val dao: NoteDao) : NoteRepository {

    override fun getAllNotesWithPictures(): Flow<List<NoteEntityWithPictures>> = dao.getAllNotesWithPictures().flowOn(Dispatchers.IO)

    override fun getAllDeletedNotesWithPictures(): Flow<List<NoteEntityWithPictures>> = dao.getAllDeletedNotesWithPictures().flowOn(Dispatchers.IO)

    override fun getNoteById(id: Long): Flow<NoteEntityWithPictures> = dao.getNoteById(id).flowOn(Dispatchers.IO)

    override suspend fun upsertNoteEntity(noteEntity: NoteEntity): Long = dao.upsertNoteEntity(noteEntity)
}