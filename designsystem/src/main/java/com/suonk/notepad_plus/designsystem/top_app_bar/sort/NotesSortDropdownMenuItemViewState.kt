package com.suonk.notepad_plus.designsystem.top_app_bar.sort


data class NotesSortDropdownMenuItemViewState(
    val text: String,
    val textResource: Int,
    val hasDivider: Boolean,
    val isSelected: Boolean,
    val onSelectedChanged: (Boolean, Int) -> Unit,
)