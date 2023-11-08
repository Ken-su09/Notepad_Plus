package com.suonk.notepad_plus.ui.note.details

import com.suonk.notepad_plus.utils.NativeText

sealed class NoteDetailsUiEvent {
    data class ShowToast(val message: NativeText) : NoteDetailsUiEvent()
    object ActionFinish : NoteDetailsUiEvent()
}