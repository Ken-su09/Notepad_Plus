package com.suonk.notepad_plus.domain.note.delete_note

interface DeleteNoteRepository {

    suspend fun deleteNoteEntityById(id: Long)
}