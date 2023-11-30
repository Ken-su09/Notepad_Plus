package com.suonk.notepad_plus.design_system.top_app_bar.filter

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp

@Composable
fun NotesFilterDropdownMenuItem(model: NotesFilterDropdownMenuItemViewState) {
    var isCheckedState by rememberSaveable { mutableStateOf(model.isChecked) }

    DropdownMenuItem(
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = model.text,
                    modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                Checkbox(
                    checked = isCheckedState,
                    onCheckedChange = {
                        isCheckedState = !isCheckedState
                        model.onCheckedChanged(isCheckedState, model.textResource)
                    },
                    modifier = Modifier.align(Alignment.CenterVertically).weight(3f)
                )
            }
        },
        onClick = {
            isCheckedState = !isCheckedState
            model.onCheckedChanged(isCheckedState, model.textResource)
        },
        modifier = Modifier.fillMaxWidth()
    )
    if (model.hasDivider) {
        Divider()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNotesDropdownMenuItem(@PreviewParameter(NotesDropdownMenuItemViewStateProvider::class) model: NotesFilterDropdownMenuItemViewState) {
    NotesFilterDropdownMenuItem(model)
}

class NotesDropdownMenuItemViewStateProvider : PreviewParameterProvider<NotesFilterDropdownMenuItemViewState> {
    override val values = List(4) { index ->
        val hasDivider = index % 2 == 0
        val isChecked = (index + 1) % 2 == 0
        NotesFilterDropdownMenuItemViewState(
            text = "Text: $index",
            textResource = 0,
            hasDivider = hasDivider,
            isChecked = isChecked,
            onCheckedChanged = { isCheck, textRessource ->
            }
        )
    }.asSequence()
}