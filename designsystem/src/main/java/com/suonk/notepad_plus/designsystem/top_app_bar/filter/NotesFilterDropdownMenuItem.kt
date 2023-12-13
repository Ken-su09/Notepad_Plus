package com.suonk.notepad_plus.designsystem.top_app_bar.filter

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
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.ui.note.list.NotesListViewModel

@Composable
fun NotesFilterDropdownMenuItem(viewModel: NotesListViewModel, model: NotesFilterDropdownMenuItemViewState, defaultIntFilterState: MutableList<Int>) {
    DropdownMenuItem(
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = model.text,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                RadioButton(
                    selected = defaultIntFilterState.contains(model.textResource),
                    onClick = {
                        Log.i("FilterList", "onCheckedChange : defaultIntFilterState 1 : $defaultIntFilterState")

                        if (model.textResource == R.string.remove_filter) {
                            defaultIntFilterState.clear()
                            defaultIntFilterState.add(model.textResource)
                            viewModel.setCurrentSortFilter(model.textResource)
                        } else {
                            if (defaultIntFilterState.contains(R.string.remove_filter)) {
                                defaultIntFilterState.remove(R.string.remove_filter)
                            }
                            defaultIntFilterState.add(model.textResource)
                            viewModel.setCurrentSortFilter(model.textResource)
                        }
                        Log.i("FilterList", "onCheckedChange : defaultIntFilterState 2 : $defaultIntFilterState")
                        Log.i("FilterList", "onCheckedChange : model.textResource : ${model.textResource}")
                    },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(3f)
                )
            }
        },
        onClick = {
            Log.i("FilterList", "onClick defaultIntFilterState 1 : $defaultIntFilterState")

            if (model.textResource == R.string.remove_filter) {
                defaultIntFilterState.clear()
                defaultIntFilterState.add(model.textResource)
                viewModel.setCurrentSortFilter(model.textResource)
            } else {
                if (defaultIntFilterState.contains(R.string.remove_filter)) {
                    defaultIntFilterState.remove(R.string.remove_filter)
                }
                defaultIntFilterState.add(model.textResource)
                viewModel.setCurrentSortFilter(model.textResource)
            }
            Log.i("FilterList", "onClick defaultIntFilterState 2 : $defaultIntFilterState")
            Log.i("FilterList", "onClick model.textResource : ${model.textResource}")
        },
        modifier = Modifier.fillMaxWidth()
    )
    if (model.hasDivider) {
        Divider()
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewNotesDropdownMenuItem(@PreviewParameter(NotesDropdownMenuItemViewStateProvider::class) model: NotesFilterDropdownMenuItemViewState) {
//    NotesFilterDropdownMenuItem(model, defaultIntState)
//}

class NotesDropdownMenuItemViewStateProvider : PreviewParameterProvider<NotesFilterDropdownMenuItemViewState> {
    override val values = List(4) { index ->
        val hasDivider = index % 2 == 0
        NotesFilterDropdownMenuItemViewState(
            text = "Text: $index",
            textResource = 0,
            hasDivider = hasDivider,
        )
    }.asSequence()
}