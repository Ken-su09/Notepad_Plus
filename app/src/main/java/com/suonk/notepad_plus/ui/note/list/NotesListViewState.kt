package com.suonk.notepad_plus.ui.note.list

import com.suonk.notepad_plus.utils.EquatableCallback

data class NotesListViewState(
    val id: Long,
    val title: String,
    val content: String,
    val date: String,
    val color: Int,
    val onItemNoteClicked: EquatableCallback,
    val onDeleteNoteClicked: EquatableCallback,
    val actions: List<NotesListActionViewState>,
)