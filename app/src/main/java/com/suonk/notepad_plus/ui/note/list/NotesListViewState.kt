package com.suonk.notepad_plus.ui.note.list

data class NotesListViewState(
    val id: Long,
    val title: String,
    val content: String,
    val date: String
) {
}