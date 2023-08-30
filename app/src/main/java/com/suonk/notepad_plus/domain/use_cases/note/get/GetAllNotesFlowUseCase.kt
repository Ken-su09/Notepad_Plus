package com.suonk.notepad_plus.domain.use_cases.note.get

import com.suonk.notepad_plus.domain.repositories.NoteRepository
import com.suonk.notepad_plus.model.database.data.entities.NoteEntityWithPictures
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllNotesFlowUseCase @Inject constructor(private val noteRepository: NoteRepository) {

    fun invoke(): Flow<List<NoteEntityWithPictures>> = noteRepository.getAllNotesWithPictures()
}