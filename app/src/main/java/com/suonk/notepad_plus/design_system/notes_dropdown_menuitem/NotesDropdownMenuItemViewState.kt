package com.suonk.notepad_plus.design_system.notes_dropdown_menuitem

data class NotesDropdownMenuItemViewState(
    val text: String,
    val hasDivider: Boolean,
    val isChecked: Boolean,
    val onCheckedChanged: (Boolean) -> Unit,
)