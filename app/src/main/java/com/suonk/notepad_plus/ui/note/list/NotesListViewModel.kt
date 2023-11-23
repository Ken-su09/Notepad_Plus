package com.suonk.notepad_plus.ui.note.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suonk.notepad_plus.R
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

    private val getSortingParametersUseCase: GetSortingParametersUseCase,

    getCurrentSortFilterNoteUseCase: GetCurrentSortFilterUseCase,
    private val setCurrentSortFilterNoteUseCase: SetCurrentSortFilterUseCase,

    private val upsertNoteUseCase: UpsertNoteUseCase,
    private val setCurrentNoteIdUseCase: SetCurrentNoteIdUseCase,
) : ViewModel() {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    private val _searchBarText = MutableStateFlow("")
    val searchBarText = _searchBarText.asStateFlow()

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
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds), emptyList())

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
        })

    private fun onDeleteNoteMenuItemClicked(entity: NoteEntityWithPictures) {
        viewModelScope.launch {
            upsertNoteUseCase.invoke(
                NoteEntity(
                    id = entity.noteEntity.id,
                    title = entity.noteEntity.title,
                    content = entity.noteEntity.content,
                    date = entity.noteEntity.date,
                    color = entity.noteEntity.color,
                    isFavorite = entity.noteEntity.isFavorite,
                    isDeleted = true,
                )
            )
        }
    }

    fun setSearchParameters(search: String?) {
        search?.let {
            _searchBarText.value = it
        }
        setSearchNoteUseCase.invoke(search)
    }

    fun setCurrentSortFilterParameters(itemId: Int) {
        Log.i("SortNote", "viewModel itemId : $itemId")
        setCurrentSortFilterNoteUseCase.invoke(itemId)
    }
}