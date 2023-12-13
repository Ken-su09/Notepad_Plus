package com.suonk.notepad_plus.designsystem.top_app_bar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.designsystem.top_app_bar.filter.NotesFilterDropdownMenuItem
import com.suonk.notepad_plus.designsystem.top_app_bar.sort.NotesSortDropdownMenuItem
import com.suonk.notepad_plus.ui.note.list.NotesListViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(modifier: Modifier = Modifier, viewModel: NotesListViewModel) {
    CenterAlignedTopAppBar(
        title = { }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), actions = {
            SearchBar(
                modifier = Modifier
                    .weight(2f)
                    .padding(16.dp)
                    .heightIn(max = 56.dp), viewModel
            )

            NotesDropdownMenu(
                Modifier
                    .weight(1f)
                    .heightIn(max = 56.dp), viewModel
            )
        }, modifier = modifier
    )
}

@Composable
private fun NotesDropdownMenu(modifier: Modifier = Modifier, viewModel: NotesListViewModel) {
    var sortMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var filterMenuExpanded by rememberSaveable { mutableStateOf(false) }
    val defaultIntSortState = rememberSaveable { mutableIntStateOf(R.string.date_asc) }
    val defaultIntFilterState = rememberSaveable { mutableListOf(R.string.remove_filter) }

    Row(
        modifier = modifier
    ) {
        IconButton(onClick = {
            sortMenuExpanded = !sortMenuExpanded
            filterMenuExpanded = !sortMenuExpanded
        }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_sort), contentDescription = stringResource(R.string.toolbar_sort)
            )
        }
        IconButton(onClick = {
            filterMenuExpanded = !filterMenuExpanded
            sortMenuExpanded = !filterMenuExpanded
        }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_filter),
                contentDescription = stringResource(R.string.toolbar_filter),
            )
        }
        DropdownMenu(
            expanded = sortMenuExpanded,
            onDismissRequest = { sortMenuExpanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            val sortItems by viewModel.listOfSortItems.collectAsState()

            sortItems.forEach { sortItem ->
                NotesSortDropdownMenuItem(viewModel, sortItem, defaultIntSortState)
            }
        }
        DropdownMenu(
            expanded = filterMenuExpanded,
            onDismissRequest = { filterMenuExpanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            val filterItems by viewModel.listOfFilterItems.collectAsState()
            filterItems.forEach { filterItem ->
                NotesFilterDropdownMenuItem(viewModel, filterItem, defaultIntFilterState)
            }
        }
    }
}

@Composable
private fun SearchBar(modifier: Modifier = Modifier, viewModel: NotesListViewModel) {
    val searchBarText by viewModel.searchBarText.collectAsState()

    TextField(value = searchBarText, label = { Text("Search Note") }, onValueChange = { searchText ->
        viewModel.setSearchParameters(searchText)
    }, leadingIcon = {
        Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon")
    }, textStyle = TextStyle(color = Color(0xFF484644)), modifier = modifier
    )
}