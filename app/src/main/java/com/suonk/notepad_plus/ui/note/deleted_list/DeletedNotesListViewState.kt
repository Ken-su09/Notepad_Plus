package com.suonk.notepad_plus.ui.note.deleted_list

import com.suonk.notepad_plus.domain.ColorEntity
import com.suonk.notepad_plus.utils.EquatableCallback

data class DeletedNotesListViewState(
    val id: Long,
    val title: String,
    val content: String,
    val date: String,
    val color: ColorEntity,
    val onItemNoteClicked: EquatableCallback,
    val onRestoreNoteClicked: EquatableCallback,
)