package com.suonk.notepad_plus.designsystem.top_app_bar.sort

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suonk.notepad_plus.designsystem.top_app_bar.SortingEntity

@Composable
fun NotesSortDropdownMenuItem(
    onSortItemSelected: (SortingEntity) -> Unit,
    sortItem: NotesSortDropdownMenuItemViewState,
    defaultIntSortState: MutableIntState
) {
    DropdownMenuItem(text = {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = sortItem.text.toString())
            Spacer(modifier = Modifier.width(8.dp))
            RadioButton(selected = defaultIntSortState.intValue == sortItem.textResource,
                onClick = {
                    defaultIntSortState.intValue = sortItem.textResource
                    onSortItemSelected(sortItem.sortingType)
                })
        }
    }, onClick = {
        defaultIntSortState.intValue = sortItem.textResource
        onSortItemSelected(sortItem.sortingType)
    }, modifier = Modifier.fillMaxWidth()
    )
    if (sortItem.hasDivider) {
        Divider()
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewNotesDropdownMenuItem(@PreviewParameter(NotesDropdownMenuItemViewStateProvider::class) model: NotesSortDropdownMenuItemViewState) {
//    NotesSortDropdownMenuItem(model)
//}

//class NotesDropdownMenuItemViewStateProvider : PreviewParameterProvider<NotesSortDropdownMenuItemViewState> {
//    override val values = List(4) { index ->
//        val hasDivider = index % 2 == 0
//        val isSelected = (index + 1) % 2 == 0
//        NotesSortDropdownMenuItemViewState(
//            text = "Text: $index",
//            textResource = 0,
//            hasDivider = hasDivider,
//        )
//    }.asSequence()
//}