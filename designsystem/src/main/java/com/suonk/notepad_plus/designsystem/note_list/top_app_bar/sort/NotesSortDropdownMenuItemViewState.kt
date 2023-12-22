package com.suonk.notepad_plus.designsystem.note_list.top_app_bar.sort

import com.suonk.notepad_plus.designsystem.note_list.top_app_bar.SortingViewState
import com.suonk.notepad_plus.designsystem.utils.NativeText

data class NotesSortDropdownMenuItemViewState(
    val text: NativeText,
    val textResource: Int,
    val hasDivider: Boolean,
    val sortingType: SortingViewState
)