package com.suonk.notepad_plus.domain.note.get

import com.suonk.mynotepad.domain.repositories.NoteRepository
import com.suonk.mynotepad.model.database.data.entities.NoteEntityWithPictures
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllNotesFlowUseCase @Inject constructor(private val noteRepository: NoteRepository) {

    fun invoke(): Flow<List<NoteEntityWithPictures>> {
        return noteRepository.getAllNotesWithPictures()
    }
}