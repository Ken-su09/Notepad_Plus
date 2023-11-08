package com.suonk.notepad_plus.ui.note.details

import androidx.compose.ui.focus.FocusState

sealed class NoteDetailsDataEvent {
    data class ChangeTitle(val value: String) : NoteDetailsDataEvent()
    data class ChangeTitleFocus(val focusState: FocusState) : NoteDetailsDataEvent()
    data class ChangeContent(val value: String) : NoteDetailsDataEvent()
    data class ChangeContentFocus(val focusState: FocusState) : NoteDetailsDataEvent()
    data class ChangeColor(val color: Long) : NoteDetailsDataEvent()
    data class SaveDataNote(val title: String, val content: String, val updatedColor: Long) : NoteDetailsDataEvent()

//    object SaveDataNote : NoteDetailsDataEvent()
}