package com.suonk.notepad_plus.ui.note.deleted_list

import com.suonk.notepad_plus.utils.EquatableCallback

data class DeletedNotesListViewState(
    val id: Long,
    val title: String,
    val content: String,
    val date: String,
    val color: Long,
    val onItemNoteClicked: EquatableCallback,
    val onRestoreNoteClicked: EquatableCallback,
)