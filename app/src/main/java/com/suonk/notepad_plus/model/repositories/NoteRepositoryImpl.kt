package com.suonk.notepad_plus.model.repositories

import com.suonk.mynotepad.domain.repositories.NoteRepository
import com.suonk.notepad_plus.model.database.dao.NoteDao
import com.suonk.mynotepad.model.database.data.entities.NoteEntityWithPictures
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl @Inject constructor(private val dao: NoteDao) : NoteRepository {
    
    override fun getAllNotesWithPictures(): Flow<List<NoteEntityWithPictures>> = dao.getAllNotesWithPictures()
}