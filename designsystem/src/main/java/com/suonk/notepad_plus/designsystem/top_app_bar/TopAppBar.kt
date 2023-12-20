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
import com.suonk.notepad_plus.designsystem.top_app_bar.filter.NotesFilterDropdownMenuItemViewState
import com.suonk.notepad_plus.designsystem.top_app_bar.sort.NotesSortDropdownMenuItem
import com.suonk.notepad_plus.designsystem.top_app_bar.sort.NotesSortDropdownMenuItemViewState
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
            listOfSortItems().forEach { sortItem ->
                NotesSortDropdownMenuItem(onSortItemSelected, sortItem, defaultIntSortState)
            }
        }
        DropdownMenu(
            expanded = filterMenuExpanded,
            onDismissRequest = { filterMenuExpanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            listOfFilterItems().forEach { filterItem ->
                NotesFilterDropdownMenuItem(onFilterItemChecked, filterItem, defaultIntFilterState)
            }
        }
    }
}

@Composable
private fun SearchBar(modifier: Modifier = Modifier, onSearchTextChanged: (String) -> Unit, viewModel: TopAppBarViewModel) {
    val searchBarText by viewModel.searchBarText.collectAsState()

    TextField(value = searchBarText, label = { Text("Search Note") }, onValueChange = { searchText ->
        onSearchTextChanged(searchText)
    }, leadingIcon = {
        Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon")
    }, textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF484644)), modifier = modifier
    )
}

private fun listOfFilterItems() = listOf(
    NotesFilterDropdownMenuItemViewState(
        text = NativeText.Resource(R.string.remove_filter),
        textResource = R.string.remove_filter,
        hasDivider = true,
        filterType = FilteringEntity.REMOVE_FILTER
    ),
    NotesFilterDropdownMenuItemViewState(
        text = NativeText.Resource(R.string.orange),
        textResource = R.string.orange,
        hasDivider = false,
        filterType = FilteringEntity.ORANGE
    ),
    NotesFilterDropdownMenuItemViewState(
        text = NativeText.Resource(R.string.pink),
        textResource = R.string.pink,
        hasDivider = false,
        filterType = FilteringEntity.PINK
    ),
    NotesFilterDropdownMenuItemViewState(
        text = NativeText.Resource(R.string.green),
        textResource = R.string.green,
        hasDivider = false,
        filterType = FilteringEntity.GREEN
    ),
    NotesFilterDropdownMenuItemViewState(
        text = NativeText.Resource(R.string.yellow),
        textResource = R.string.yellow,
        hasDivider = false,
        filterType = FilteringEntity.YELLOW
    ),
    NotesFilterDropdownMenuItemViewState(
        text = NativeText.Resource(R.string.purple),
        textResource = R.string.purple,
        hasDivider = false,
        filterType = FilteringEntity.PURPLE
    ),
    NotesFilterDropdownMenuItemViewState(
        text = NativeText.Resource(R.string.blue),
        textResource = R.string.blue,
        hasDivider = false,
        filterType = FilteringEntity.BLUE
    ),
)

enum class FilteringEntity(resource: NativeText.Resource, textResource: Int, hasDivider: Boolean) {
    REMOVE_FILTER(NativeText.Resource(R.string.remove_filter), R.string.remove_filter, true),
    ORANGE(NativeText.Resource(R.string.pink), R.string.pink, false),
    PINK(NativeText.Resource(R.string.green), R.string.green, false),
    GREEN(NativeText.Resource(R.string.orange), R.string.orange, false),
    YELLOW(NativeText.Resource(R.string.yellow), R.string.yellow, false),
    PURPLE(NativeText.Resource(R.string.purple), R.string.purple, false),
    BLUE(NativeText.Resource(R.string.blue), R.string.blue, false),
}

private fun listOfSortItems() = listOf(
    NotesSortDropdownMenuItemViewState(
        text = NativeText.Resource(R.string.date_asc),
        textResource = R.string.date_asc,
        hasDivider = false,
        sortingType = SortingEntity.DATE_ASC
    ),
    NotesSortDropdownMenuItemViewState(
        text = NativeText.Resource(R.string.date_desc),
        textResource = R.string.date_desc,
        hasDivider = true,
        sortingType = SortingEntity.DATE_DESC
    ),
    NotesSortDropdownMenuItemViewState(
        text = NativeText.Resource(R.string.title_asc),
        textResource = R.string.title_asc,
        hasDivider = false,
        sortingType = SortingEntity.TITLE_ASC
    ),
    NotesSortDropdownMenuItemViewState(
        text = NativeText.Resource(R.string.title_desc),
        textResource = R.string.title_desc,
        hasDivider = true,
        sortingType = SortingEntity.TITLE_DESC
    ),
    NotesSortDropdownMenuItemViewState(
        text = NativeText.Resource(R.string.content_a_z),
        textResource = R.string.content_a_z,
        hasDivider = false,
        sortingType = SortingEntity.CONTENT_ASC
    ),
    NotesSortDropdownMenuItemViewState(
        text = NativeText.Resource(R.string.content_z_a),
        textResource = R.string.content_z_a,
        hasDivider = true,
        sortingType = SortingEntity.CONTENT_DESC
    ),
    NotesSortDropdownMenuItemViewState(
        text = NativeText.Resource(R.string.by_color),
        textResource = R.string.by_color,
        hasDivider = true,
        sortingType = SortingEntity.COLOR_ASC
    ),
)

enum class SortingEntity(resource: NativeText.Resource, textResource: Int, hasDivider: Boolean) {
    DATE_ASC(NativeText.Resource(R.string.date_asc), R.string.date_asc, false),
    DATE_DESC(NativeText.Resource(R.string.date_desc), R.string.date_desc, true),

    TITLE_ASC(NativeText.Resource(R.string.title_asc), R.string.title_asc, false),
    TITLE_DESC(NativeText.Resource(R.string.title_desc), R.string.title_desc, true),

    CONTENT_ASC(NativeText.Resource(R.string.content_a_z), R.string.content_a_z, false),
    CONTENT_DESC(NativeText.Resource(R.string.content_z_a), R.string.content_z_a, true),

    COLOR_ASC(NativeText.Resource(R.string.by_color), R.string.by_color, false),
}