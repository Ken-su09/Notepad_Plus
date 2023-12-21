package com.suonk.notepad_plus.ui.note.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suonk.notepad_plus.designsystem.top_app_bar.FilteringEntity
import com.suonk.notepad_plus.designsystem.top_app_bar.SortingEntity
import com.suonk.notepad_plus.domain.filter.GetFilterParametersUseCase
import com.suonk.notepad_plus.domain.sort.GetSortingParametersUseCase
import com.suonk.notepad_plus.domain.filter.SetFilterParametersUseCase
import com.suonk.notepad_plus.domain.note.get_note.GetAllNotesFlowUseCase
import com.suonk.notepad_plus.domain.note.id.SetCurrentNoteIdUseCase
import com.suonk.notepad_plus.domain.search.GetSearchNoteUseCase
import com.suonk.notepad_plus.domain.search.SetSearchNoteUseCase
import com.suonk.notepad_plus.domain.note.upsert.UpsertNoteUseCase
import com.suonk.notepad_plus.domain.sort.SetSortingParametersUseCase
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import com.suonk.notepad_plus.model.database.data.entities.NoteEntityWithPictures
import com.suonk.notepad_plus.utils.EquatableCallback
import com.suonk.notepad_plus.utils.Filtering
import com.suonk.notepad_plus.utils.Sorting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
    private val setSortingParametersUseCase: SetSortingParametersUseCase,

    getFilterParametersNoteUseCase: GetFilterParametersUseCase,
    private val setFilterParametersUseCase: SetFilterParametersUseCase,

    private val upsertNoteUseCase: UpsertNoteUseCase,
    private val setCurrentNoteIdUseCase: SetCurrentNoteIdUseCase
) : ViewModel() {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    val notesListFlow: StateFlow<List<NotesListViewState>> = combine(
        getAllNotesFlowUseCase.invoke(),
        getSearchNoteUseCase.invoke(),
        getSortingParametersUseCase.invoke(),
        getFilterParametersNoteUseCase.invoke(),
    ) { notes, search, sorting, filter ->
        notes.asSequence().filter {
            if (search != null && search != "") {
                it.noteEntity.title.contains(search, ignoreCase = true) || it.noteEntity.content.contains(search, ignoreCase = true)
            } else {
                true
            }
        }.sortedWith(sorting.comparator).map {
            transformEntityToViewState(it)
        }.filter {
            when (filter) {
                Filtering.REMOVE_FILTER -> {
                    it.id != 0L
                }

                Filtering.PINK -> {
                    it.color == 0xFF7fdeea
                }

                Filtering.PURPLE -> {
                    it.color == 0xFFd095db
                }

                Filtering.GREEN -> {
                    it.color == 0xFFd095db
                }

                Filtering.BLUE -> {
                    it.color == 0xFF7fdeea
                }

                Filtering.ORANGE -> {
                    it.color == 0xFFffab91
                }

                Filtering.YELLOW -> {
                    it.color == 0xFFe8ed9d
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
        setSearchNoteUseCase.invoke(search)
    }

    fun setCurrentFilter(filteringEntity: FilteringEntity) {
        when (filteringEntity) {
            FilteringEntity.REMOVE_FILTER -> {
                setFilterParametersUseCase.invoke(Filtering.REMOVE_FILTER)
            }

            FilteringEntity.ORANGE -> {
                setFilterParametersUseCase.invoke(Filtering.ORANGE)
            }

            FilteringEntity.PINK -> {
                setFilterParametersUseCase.invoke(Filtering.PINK)
            }

            FilteringEntity.GREEN -> {
                setFilterParametersUseCase.invoke(Filtering.GREEN)
            }

            FilteringEntity.YELLOW -> {
                setFilterParametersUseCase.invoke(Filtering.YELLOW)
            }

            FilteringEntity.PURPLE -> {
                setFilterParametersUseCase.invoke(Filtering.PURPLE)
            }

            FilteringEntity.BLUE -> {
                setFilterParametersUseCase.invoke(Filtering.BLUE)
            }

            else -> {
                setFilterParametersUseCase.invoke(Filtering.REMOVE_FILTER)
            }
        }
    }

    fun setCurrentSort(sortingEntity: SortingEntity) {
        when (sortingEntity) {
            SortingEntity.DATE_ASC -> {
                setSortingParametersUseCase.invoke(Sorting.DATE_ASC)
            }

            SortingEntity.DATE_DESC -> {
                setSortingParametersUseCase.invoke(Sorting.DATE_DESC)
            }

            SortingEntity.TITLE_ASC -> {
                setSortingParametersUseCase.invoke(Sorting.TITLE_ASC)
            }

            SortingEntity.TITLE_DESC -> {
                setSortingParametersUseCase.invoke(Sorting.TITLE_DESC)
            }

            SortingEntity.CONTENT_ASC -> {
                setSortingParametersUseCase.invoke(Sorting.CONTENT_ASC)
            }

            SortingEntity.CONTENT_DESC -> {
                setSortingParametersUseCase.invoke(Sorting.CONTENT_DESC)
            }

            SortingEntity.COLOR_ASC -> {
                setSortingParametersUseCase.invoke(Sorting.COLOR_ASC)
            }

            else -> {

            }
        }
    }
}