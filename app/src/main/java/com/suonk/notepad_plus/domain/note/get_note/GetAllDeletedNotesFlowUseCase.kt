package com.suonk.notepad_plus.domain.note.get_note

import com.suonk.notepad_plus.model.database.data.entities.NoteEntityWithPictures
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllDeletedNotesFlowUseCase @Inject constructor(private val getNotesRepository: GetNotesRepository) {

    fun invoke(): Flow<List<NoteEntityWithPictures>> = getNotesRepository.getAllDeletedNotesWithPictures()
}