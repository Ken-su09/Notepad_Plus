package com.suonk.notepad_plus.designsystem.top_app_bar.sort

import com.suonk.notepad_plus.designsystem.top_app_bar.SortingEntity
import com.suonk.notepad_plus.designsystem.utils.NativeText

data class NotesSortDropdownMenuItemViewState(
    val text: NativeText,
    val textResource: Int,
    val hasDivider: Boolean,
    val sortingType: SortingEntity
)