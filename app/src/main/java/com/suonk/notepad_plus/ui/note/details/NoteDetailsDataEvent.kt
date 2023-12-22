package com.suonk.notepad_plus.ui.note.details

import com.suonk.notepad_plus.designsystem.utils.ColorEntity

sealed class NoteDetailsDataEvent {
    data class ChangeTitle(val title: String) : NoteDetailsDataEvent()
    data class ChangeContent(val content: String) : NoteDetailsDataEvent()
    data class ChangeColor(val colorEntity: ColorEntity) : NoteDetailsDataEvent()
    object SaveNote : NoteDetailsDataEvent()
    object DeleteRestoreNote : NoteDetailsDataEvent()
}