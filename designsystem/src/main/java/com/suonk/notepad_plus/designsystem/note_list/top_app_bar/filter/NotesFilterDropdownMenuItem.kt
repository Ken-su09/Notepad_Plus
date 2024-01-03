package com.suonk.notepad_plus.designsystem.note_list.top_app_bar.filter

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.suonk.notepad_plus.designsystem.R
import com.suonk.notepad_plus.designsystem.note_list.top_app_bar.FilteringViewState
import com.suonk.notepad_plus.designsystem.note_list.top_app_bar.TopAppBarViewModel
import com.suonk.notepad_plus.designsystem.utils.toCharSequence

@Composable
fun NotesFilterDropdownMenuItem(
    onFilterItemChecked: (FilteringViewState) -> Unit,
    filterItem: NotesFilterDropdownMenuItemViewState,
    defaultIntFilterState: MutableList<Int>
) {
    DropdownMenuItem(
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = filterItem.text.toCharSequence(LocalContext.current).toString(),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                RadioButton(
                    selected = defaultIntFilterState.contains(filterItem.textResource),
                    onClick = {
                        if (filterItem.textResource == R.string.remove_filter) {
                            defaultIntFilterState.clear()
                            defaultIntFilterState.add(filterItem.textResource)
                            onFilterItemChecked(filterItem.filterType)
                        } else {
                            if (defaultIntFilterState.contains(R.string.remove_filter)) {
                                defaultIntFilterState.remove(R.string.remove_filter)
                            }
                            defaultIntFilterState.add(filterItem.textResource)
                            onFilterItemChecked(filterItem.filterType)
                        }
                        Log.i("GetFilter", "defaultIntFilterState 3 : $defaultIntFilterState")
                    },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(3f)
                )
            }
        },
        onClick = {
            if (filterItem.textResource == R.string.remove_filter) {
                defaultIntFilterState.clear()
                defaultIntFilterState.add(filterItem.textResource)
                onFilterItemChecked(filterItem.filterType)
            } else {
                if (defaultIntFilterState.contains(R.string.remove_filter)) {
                    defaultIntFilterState.remove(R.string.remove_filter)
                }
                defaultIntFilterState.add(filterItem.textResource)
                onFilterItemChecked(filterItem.filterType)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
    if (filterItem.hasDivider) {
        Divider()
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewNotesDropdownMenuItem(@PreviewParameter(NotesDropdownMenuItemViewStateProvider::class) model: NotesFilterDropdownMenuItemViewState) {
//    NotesFilterDropdownMenuItem(model, defaultIntState)
//}

//class NotesDropdownMenuItemViewStateProvider : PreviewParameterProvider<NotesFilterDropdownMenuItemViewState> {
//    override val values = List(4) { index ->
//        val hasDivider = index % 2 == 0
//        NotesFilterDropdownMenuItemViewState(
//            text = "Text: $index",
//            textResource = 0,
//            hasDivider = hasDivider,
//        )
//    }.asSequence()
//}