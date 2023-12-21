package com.suonk.notepad_plus.domain.filter

import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import com.suonk.notepad_plus.domain.ColorEntity

enum class FilterEntity(val isMatching: (NoteEntity) -> Boolean) {
    REMOVE_FILTER({ true }),
    PINK({ it.color == ColorEntity.PINK })
}