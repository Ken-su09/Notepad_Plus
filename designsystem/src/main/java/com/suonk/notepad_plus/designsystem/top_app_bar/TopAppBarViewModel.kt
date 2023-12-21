package com.suonk.notepad_plus.designsystem.top_app_bar

import androidx.lifecycle.ViewModel
import com.suonk.notepad_plus.designsystem.R
import com.suonk.notepad_plus.designsystem.top_app_bar.filter.NotesFilterDropdownMenuItemViewState
import com.suonk.notepad_plus.designsystem.top_app_bar.sort.NotesSortDropdownMenuItemViewState
import com.suonk.notepad_plus.designsystem.utils.NativeText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TopAppBarViewModel : ViewModel() {

    private val _listOfSortItems = MutableStateFlow(listOfSortItems())
    val listOfSortItems: StateFlow<List<NotesSortDropdownMenuItemViewState>> = _listOfSortItems.asStateFlow()

    private val _listOfFilterItems = MutableStateFlow(listOfFilterItems())
    val listOfFilterItems: StateFlow<List<NotesFilterDropdownMenuItemViewState>> = _listOfFilterItems.asStateFlow()

    private val _searchBarText = MutableStateFlow("")
    val searchBarText = _searchBarText.asStateFlow()

    fun setSearchParameters(search: String?) {
        search?.let {
            _searchBarText.value = it
        }
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

}