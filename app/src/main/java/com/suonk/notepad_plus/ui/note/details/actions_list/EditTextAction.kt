package com.suonk.notepad_plus.ui.note.details.actions_list

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import com.suonk.notepad_plus.utils.EquatableCallback

data class EditTextAction(
    @DrawableRes
    val icon: Int,
    @DrawableRes
    val background: Int?,
    val onClickedCallback: EquatableCallback,
)
