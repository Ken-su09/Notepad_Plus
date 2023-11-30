package com.suonk.notepad_plus.ui.note.details

sealed class NoteDetailsDataEvent {
    data class ChangeTitle(val title: String) : NoteDetailsDataEvent()
    data class ChangeContent(val content: String) : NoteDetailsDataEvent()
    data class ChangeColor(val color: Long) : NoteDetailsDataEvent()
    object SaveNote : NoteDetailsDataEvent()
    object DeleteRestoreNote : NoteDetailsDataEvent()
}