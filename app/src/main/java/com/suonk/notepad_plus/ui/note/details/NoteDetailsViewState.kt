package com.suonk.notepad_plus.ui.note.details

import com.suonk.notepad_plus.ui.note.details.actions_list.EditTextAction
import com.suonk.notepad_plus.utils.NativeText
import java.time.Instant

data class NoteDetailsViewState(
    val id: Long,
    val title: String,
    val content: String,
    val dateText: NativeText,
    val dateValue: Instant?,
    val actions: List<EditTextAction>
)