package com.suonk.notepad_plus.ui.note.list

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.designsystem.top_app_bar.filter.NotesFilterDropdownMenuItemViewState
import com.suonk.notepad_plus.designsystem.top_app_bar.sort.NotesSortDropdownMenuItemViewState
import com.suonk.notepad_plus.domain.use_cases.note.filter_sort.GetCurrentSortFilterUseCase
import com.suonk.notepad_plus.domain.use_cases.note.filter_sort.GetSortingParametersUseCase
import com.suonk.notepad_plus.domain.use_cases.note.filter_sort.SetCurrentSortFilterUseCase
import com.suonk.notepad_plus.domain.use_cases.note.get_note.GetAllNotesFlowUseCase
import com.suonk.notepad_plus.domain.use_cases.note.id.SetCurrentNoteIdUseCase
import com.suonk.notepad_plus.domain.use_cases.note.search.GetSearchNoteUseCase
import com.suonk.notepad_plus.domain.use_cases.note.search.SetSearchNoteUseCase
import com.suonk.notepad_plus.domain.use_cases.note.upsert.UpsertNoteUseCase
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import com.suonk.notepad_plus.model.database.data.entities.NoteEntityWithPictures
import com.suonk.notepad_plus.utils.EquatableCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class NotesListViewModel @Inject constructor(
    getAllNotesFlowUseCase: GetAllNotesFlowUseCase,

    getSearchNoteUseCase: GetSearchNoteUseCase,
    private val setSearchNoteUseCase: SetSearchNoteUseCase,

    getSortingParametersUseCase: GetSortingParametersUseCase,

    getCurrentSortFilterNoteUseCase: GetCurrentSortFilterUseCase,
    private val setCurrentSortFilterNoteUseCase: SetCurrentSortFilterUseCase,

    private val upsertNoteUseCase: UpsertNoteUseCase,
    private val setCurrentNoteIdUseCase: SetCurrentNoteIdUseCase,
    private val application: Application
) : ViewModel() {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    private val _searchBarText = MutableStateFlow("")
    val searchBarText = _searchBarText.asStateFlow()

    private val _listOfSortItems = MutableStateFlow(listOfSortItems())
    val listOfSortItems: StateFlow<List<NotesSortDropdownMenuItemViewState>> = _listOfSortItems.asStateFlow()

    private val _listOfFilterItems = MutableStateFlow(listOfFilterItems())
    val listOfFilterItems: StateFlow<List<NotesFilterDropdownMenuItemViewState>> = _listOfFilterItems.asStateFlow()

    val notesListFlow: StateFlow<List<NotesListViewState>> = combine(
        getAllNotesFlowUseCase.invoke(),
        getSearchNoteUseCase.invoke(),
        getSortingParametersUseCase.invoke(),
        getCurrentSortFilterNoteUseCase.invoke(),
    ) { notes, search, sorting, filterId ->
        notes.asSequence().filter {
            if (search != null && search != "") {
                it.noteEntity.title.contains(search, ignoreCase = true) || it.noteEntity.content.contains(search, ignoreCase = true)
            } else {
                true
            }
        }.sortedWith(sorting.comparator).map {
            transformEntityToViewState(it)
        }.filter {
            when (filterId) {
                R.string.remove_filter -> {
                    it.id != 0L
                }

                R.string.pink -> {
                    it.color == 0xFF7fdeea
                }

                R.string.purple -> {
                    it.color == 0xFFd095db
                }

                R.string.green -> {
                    it.color == 0xFFd095db
                }

                R.string.blue -> {
                    it.color == 0xFF7fdeea
                }

                R.string.orange -> {
                    it.color == 0xFFffab91
                }

                R.string.yellow -> {
                    it.color == 0xFFe8ed9d
                }

                else -> {
                    it.id != 0L
                }
            }
        }.toList()
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds), emptyList()
    )

    private fun transformEntityToViewState(entity: NoteEntityWithPictures) = NotesListViewState(
        id = entity.noteEntity.id,
        title = entity.noteEntity.title,
        content = entity.noteEntity.content,
        date = entity.noteEntity.date.format(dateTimeFormatter),
        color = entity.noteEntity.color,
        onItemNoteClicked = EquatableCallback {
            setCurrentNoteIdUseCase.invoke(entity.noteEntity.id)
        },
        onDeleteNoteClicked = EquatableCallback {
            onDeleteNoteMenuItemClicked(entity)
        },
        actions = emptyList(),
    )

    private fun onDeleteNoteMenuItemClicked(entityWithPictures: NoteEntityWithPictures) {
        viewModelScope.launch {
            val entity = NoteEntity(
                id = entityWithPictures.noteEntity.id,
                title = entityWithPictures.noteEntity.title,
                content = entityWithPictures.noteEntity.content,
                date = entityWithPictures.noteEntity.date,
                color = entityWithPictures.noteEntity.color,
                isFavorite = entityWithPictures.noteEntity.isFavorite,
                isDeleted = true,
            )
            upsertNoteUseCase.invoke(entity)
        }
    }

    fun setSearchParameters(search: String?) {
        search?.let {
            _searchBarText.value = it
        }
        setSearchNoteUseCase.invoke(search)
    }

    fun setCurrentSortFilter(textResource: Int) {
        setCurrentSortFilterNoteUseCase.invoke(textResource)
    }

    private fun listOfSortItems() = listOf(
        NotesSortDropdownMenuItemViewState(
            text = application.getString(R.string.date_asc),
            textResource = R.string.date_asc,
            hasDivider = false
        ),
        NotesSortDropdownMenuItemViewState(
            text = application.getString(R.string.date_desc),
            textResource = R.string.date_desc,
            hasDivider = true
        ),
        NotesSortDropdownMenuItemViewState(
            text = application.getString(R.string.title_asc),
            textResource = R.string.title_asc,
            hasDivider = false
        ),
        NotesSortDropdownMenuItemViewState(
            text = application.getString(R.string.title_desc),
            textResource = R.string.title_desc,
            hasDivider = true
        ),
        NotesSortDropdownMenuItemViewState(
            text = application.getString(R.string.content_a_z),
            textResource = R.string.content_a_z,
            hasDivider = false
        ),
        NotesSortDropdownMenuItemViewState(
            text = application.getString(R.string.content_z_a),
            textResource = R.string.content_z_a,
            hasDivider = true
        ),
        NotesSortDropdownMenuItemViewState(
            text = application.getString(R.string.by_color),
            textResource = R.string.by_color,
            hasDivider = true
        ),
    )

    private fun listOfFilterItems() = listOf(
        NotesFilterDropdownMenuItemViewState(
            text = application.getString(R.string.remove_filter),
            textResource = R.string.remove_filter,
            hasDivider = true
        ),
        NotesFilterDropdownMenuItemViewState(
            text = application.getString(R.string.pink),
            textResource = R.string.pink,
            hasDivider = false
        ),
        NotesFilterDropdownMenuItemViewState(
            text = application.getString(R.string.green),
            textResource = R.string.green,
            hasDivider = false
        ),
        NotesFilterDropdownMenuItemViewState(
            text = application.getString(R.string.orange),
            textResource = R.string.orange,
            hasDivider = false
        ),
        NotesFilterDropdownMenuItemViewState(
            text = application.getString(R.string.yellow),
            textResource = R.string.yellow,
            hasDivider = false
        ),
        NotesFilterDropdownMenuItemViewState(
            text = application.getString(R.string.purple),
            textResource = R.string.purple,
            hasDivider = false
        ),
        NotesFilterDropdownMenuItemViewState(
            text = application.getString(R.string.blue),
            textResource = R.string.blue,
            hasDivider = false
        ),
    )
}