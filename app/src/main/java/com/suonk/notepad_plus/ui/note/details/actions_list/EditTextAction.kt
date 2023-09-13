package com.suonk.notepad_plus.ui.note.details.actions_list

import android.graphics.drawable.Drawable
import com.suonk.notepad_plus.utils.EquatableCallback

data class EditTextAction(val icon: Int, val onClickedCallback: EquatableCallback, var background: Drawable?)
