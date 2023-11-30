package com.suonk.notepad_plus.designsystem.top_app_bar.sort

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.suonk.notepad_plus.R

@Composable
fun NotesSortDropdownMenuItem(model: NotesSortDropdownMenuItemViewState) {
    var isSelectedState by rememberSaveable { mutableStateOf(model.isSelected) }
    var defaultIntState by rememberSaveable { mutableIntStateOf(R.string.date_asc) }

    DropdownMenuItem(
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = model.text)
                Spacer(modifier = Modifier.width(8.dp))
                RadioButton(
                    selected = isSelectedState,
                    onClick = {
                        defaultIntState = model.textResource
                        Log.i("GestureRadioButton", "defaultIntState : $defaultIntState")
                        isSelectedState = !isSelectedState
                        model.onSelectedChanged(isSelectedState, model.textResource)
                    }
                )
            }
        },
        onClick = {
            defaultIntState = model.textResource
            Log.i("GestureRadioButton", "defaultIntState : $defaultIntState")
            isSelectedState = !isSelectedState
            model.onSelectedChanged(isSelectedState, model.textResource)
        },
        modifier = Modifier.fillMaxWidth()
    )
    if (model.hasDivider) {
        Divider()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNotesDropdownMenuItem(@PreviewParameter(NotesDropdownMenuItemViewStateProvider::class) model: NotesSortDropdownMenuItemViewState) {
    NotesSortDropdownMenuItem(model)
}

class NotesDropdownMenuItemViewStateProvider : PreviewParameterProvider<NotesSortDropdownMenuItemViewState> {
    override val values = List(4) { index ->
        val hasDivider = index % 2 == 0
        val isSelected = (index + 1) % 2 == 0
        NotesSortDropdownMenuItemViewState(
            text = "Text: $index",
            textResource = 0,
            hasDivider = hasDivider,
            isSelected = isSelected,
            onSelectedChanged = { isSelect, textResource ->
            }
        )
    }.asSequence()
}