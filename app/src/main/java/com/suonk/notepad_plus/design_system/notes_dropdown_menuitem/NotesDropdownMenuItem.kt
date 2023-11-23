package com.suonk.notepad_plus.design_system.notes_dropdown_menuitem

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

@Composable
fun NotesDropdownMenuItem(
    model: NotesDropdownMenuItemViewState,
) {
    var isCheckedState by rememberSaveable { mutableStateOf(model.isChecked) }
    DropdownMenuItem(
        text = {
            Row {
                Text(text = model.text)
                Checkbox(
                    checked = isCheckedState,
                    onCheckedChange = {
                        isCheckedState = !isCheckedState
                        model.onCheckedChanged(isCheckedState)
                    }
                )
            }
        },
        onClick = {
            isCheckedState = !isCheckedState
            model.onCheckedChanged(isCheckedState)
        }
    )
    if (model.hasDivider) {
        Divider()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNotesDropdownMenuItem(
    @PreviewParameter(NotesDropdownMenuItemViewStateProvider::class) model: NotesDropdownMenuItemViewState
) {
    NotesDropdownMenuItem(model)
}

class NotesDropdownMenuItemViewStateProvider : PreviewParameterProvider<NotesDropdownMenuItemViewState> {
    override val values = List(4) { index ->
        val hasDivider = index % 2 == 0
        val isChecked = (index + 1) % 2 == 0
        NotesDropdownMenuItemViewState(
            text = "Text: $index",
            hasDivider = hasDivider,
            isChecked = isChecked,
            onCheckedChanged = {}
        )
    }.asSequence()
}