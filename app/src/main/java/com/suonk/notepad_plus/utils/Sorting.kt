package com.suonk.notepad_plus.utils

import com.suonk.notepad_plus.model.database.data.entities.NoteEntityWithPictures
import java.time.LocalDateTime

enum class Sorting(val comparator: Comparator<NoteEntityWithPictures>) {
    DATE_ASC(Comparator.comparing { it.noteEntity.date }),
    DATE_DESC(Comparator.comparing<NoteEntityWithPictures?, LocalDateTime?> { it.noteEntity.date }.reversed()),

    TITLE_ASC(Comparator.comparing { it.noteEntity.title }),
    TITLE_DESC(Comparator.comparing<NoteEntityWithPictures?, String> { it.noteEntity.title }.reversed()),

    CONTENT_ASC(Comparator.comparing { it.noteEntity.content }),
    CONTENT_DESC(Comparator.comparing<NoteEntityWithPictures?, String> { it.noteEntity.content }.reversed()),

    COLOR_ASC(Comparator.comparing { it.noteEntity.color }),
}