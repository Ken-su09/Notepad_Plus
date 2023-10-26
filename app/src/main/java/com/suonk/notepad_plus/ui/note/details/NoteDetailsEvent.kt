package com.suonk.notepad_plus.ui.note.details

import androidx.compose.ui.focus.FocusState

sealed class NoteDetailsEvent {
    data class ChangeTitle(val value: String) : NoteDetailsEvent()
    data class ChangeTitleFocus(val focusState: FocusState) : NoteDetailsEvent()
    data class ChangeContent(val value: String) : NoteDetailsEvent()
    data class ChangeContentFocus(val focusState: FocusState) : NoteDetailsEvent()
    data class ChangeColor(val color: Long) : NoteDetailsEvent()
    object SaveNote : NoteDetailsEvent()
}