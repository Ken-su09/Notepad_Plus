package com.suonk.notepad_plus.designsystem.top_app_bar.filter

import com.suonk.notepad_plus.designsystem.top_app_bar.FilteringEntity
import com.suonk.notepad_plus.designsystem.utils.NativeText


data class NotesFilterDropdownMenuItemViewState(
    val text: NativeText,
    val textResource: Int,
    val hasDivider: Boolean,
    val filterType: FilteringEntity,
)