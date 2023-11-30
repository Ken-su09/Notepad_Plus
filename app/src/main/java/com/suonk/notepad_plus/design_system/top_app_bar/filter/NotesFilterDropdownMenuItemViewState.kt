package com.suonk.notepad_plus.design_system.top_app_bar.filter


data class NotesFilterDropdownMenuItemViewState(
    val text: String,
    val textResource: Int,
    val hasDivider: Boolean,
    val isChecked: Boolean,
    val onCheckedChanged: (Boolean, Int) -> Unit,
)