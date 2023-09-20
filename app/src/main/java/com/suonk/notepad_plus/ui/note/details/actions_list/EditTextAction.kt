package com.suonk.notepad_plus.ui.note.details.actions_list

import androidx.annotation.DrawableRes
import com.suonk.notepad_plus.ui.note.details.NoteDetailsViewModel
import com.suonk.notepad_plus.utils.EquatableCallback

data class EditTextAction(
    val textStyle: ActionsSealed,
    @DrawableRes
    val icon: Int,
    @DrawableRes
    val background: Int?,
    val enabled: Boolean,
    val onClickedCallback: EquatableCallback,
)