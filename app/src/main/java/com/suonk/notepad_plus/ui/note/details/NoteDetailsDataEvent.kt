package com.suonk.notepad_plus.ui.note.details

import androidx.compose.ui.focus.FocusState

sealed class NoteDetailsDataEvent {
    data class ChangeTitle(val title: String) : NoteDetailsDataEvent()
    data class ChangeTitleFocus(val focusState: FocusState) : NoteDetailsDataEvent()
    data class ChangeContent(val content: String) : NoteDetailsDataEvent()
    data class ChangeContentFocus(val focusState: FocusState) : NoteDetailsDataEvent()
    data class ChangeColor(val color: Long) : NoteDetailsDataEvent()
    object SaveNote : NoteDetailsDataEvent()
    object DeleteRestoreNote : NoteDetailsDataEvent()
}