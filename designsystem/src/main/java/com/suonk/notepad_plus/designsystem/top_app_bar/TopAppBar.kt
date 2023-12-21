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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.suonk.notepad_plus.designsystem.R
import com.suonk.notepad_plus.designsystem.top_app_bar.filter.NotesFilterDropdownMenuItem
import com.suonk.notepad_plus.designsystem.top_app_bar.sort.NotesSortDropdownMenuItem
import com.suonk.notepad_plus.designsystem.utils.NativeText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    modifier: Modifier = Modifier, onSearchTextChanged: (String) -> Unit, onSortItemSelected: (SortingEntity) -> Unit,
    onFilterItemChecked: (FilteringEntity) -> Unit, viewModel: TopAppBarViewModel = viewModel()
) {
    CenterAlignedTopAppBar(
        title = { }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), actions = {
            SearchBar(
                modifier = Modifier
                    .weight(2f)
                    .padding(16.dp)
                    .heightIn(max = 56.dp), onSearchTextChanged, viewModel
            )

            NotesDropdownMenu(
                Modifier
                    .weight(1f)
                    .heightIn(max = 56.dp), onSortItemSelected, onFilterItemChecked, viewModel
            )
        }, modifier = modifier
    )
}

@Composable
private fun NotesDropdownMenu(
    modifier: Modifier = Modifier,
    onSortItemSelected: (SortingEntity) -> Unit,
    onFilterItemChecked: (FilteringEntity) -> Unit,
    viewModel: TopAppBarViewModel
) {
    var sortMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var filterMenuExpanded by rememberSaveable { mutableStateOf(false) }
    val defaultIntSortState = rememberSaveable { mutableIntStateOf(R.string.date_asc) }
    val defaultIntFilterState = rememberSaveable { mutableListOf(R.string.remove_filter) }

    Row(modifier = modifier) {
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
            val listOfSortItems by viewModel.listOfSortItems.collectAsState()
            listOfSortItems.forEach { sortItem ->
                NotesSortDropdownMenuItem(onSortItemSelected, sortItem, defaultIntSortState, viewModel)
            }
        }
        DropdownMenu(
            expanded = filterMenuExpanded,
            onDismissRequest = { filterMenuExpanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            val listOfFilterItems by viewModel.listOfFilterItems.collectAsState()
            listOfFilterItems.forEach { filterItem ->
                NotesFilterDropdownMenuItem(onFilterItemChecked, filterItem, defaultIntFilterState, viewModel)
            }
        }
    }
}

@Composable
private fun SearchBar(modifier: Modifier = Modifier, onSearchTextChanged: (String) -> Unit, viewModel: TopAppBarViewModel) {
    val searchBarText by viewModel.searchBarText.collectAsState()

    TextField(value = searchBarText, label = { Text("Search Note") }, onValueChange = { searchText ->
        viewModel.setSearchParameters(searchText)
        onSearchTextChanged(searchText)
    }, leadingIcon = {
        Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon")
    }, textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF484644)), modifier = modifier
    )
}

enum class FilteringEntity(resource: NativeText.Resource, textResource: Int, hasDivider: Boolean) {
    REMOVE_FILTER(NativeText.Resource(R.string.remove_filter), R.string.remove_filter, true),
    ORANGE(NativeText.Resource(R.string.pink), R.string.pink, false),
    PINK(NativeText.Resource(R.string.green), R.string.green, false),
    GREEN(NativeText.Resource(R.string.orange), R.string.orange, false),
    YELLOW(NativeText.Resource(R.string.yellow), R.string.yellow, false),
    PURPLE(NativeText.Resource(R.string.purple), R.string.purple, false),
    BLUE(NativeText.Resource(R.string.blue), R.string.blue, false),
}
enum class SortingEntity(resource: NativeText.Resource, textResource: Int, hasDivider: Boolean) {
    DATE_ASC(NativeText.Resource(R.string.date_asc), R.string.date_asc, false),
    DATE_DESC(NativeText.Resource(R.string.date_desc), R.string.date_desc, true),

    TITLE_ASC(NativeText.Resource(R.string.title_asc), R.string.title_asc, false),
    TITLE_DESC(NativeText.Resource(R.string.title_desc), R.string.title_desc, true),

    CONTENT_ASC(NativeText.Resource(R.string.content_a_z), R.string.content_a_z, false),
    CONTENT_DESC(NativeText.Resource(R.string.content_z_a), R.string.content_z_a, true),

    COLOR_ASC(NativeText.Resource(R.string.by_color), R.string.by_color, false),
}