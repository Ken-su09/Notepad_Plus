package com.suonk.notepad_plus.domain.note.upsert

import com.suonk.notepad_plus.model.database.data.entities.NoteEntity

interface UpsertNoteRepository {

    suspend fun upsertNoteEntity(noteEntity: NoteEntity): Long
}