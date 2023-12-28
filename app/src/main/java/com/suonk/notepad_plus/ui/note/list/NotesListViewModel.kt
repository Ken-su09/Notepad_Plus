package com.suonk.notepad_plus.ui.note.list

import android.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.designsystem.note_list.top_app_bar.FilteringViewState
import com.suonk.notepad_plus.designsystem.note_list.top_app_bar.SortingViewState
import com.suonk.notepad_plus.designsystem.utils.ColorEntity
import com.suonk.notepad_plus.designsystem.utils.toARGB
import com.suonk.notepad_plus.domain.filter.FilterEntity
import com.suonk.notepad_plus.domain.filter.GetFilterParametersUseCase
import com.suonk.notepad_plus.domain.sort.GetSortingParametersUseCase
import com.suonk.notepad_plus.domain.filter.SetFilterParametersUseCase
import com.suonk.notepad_plus.domain.note.get_note.GetAllNotesFlowUseCase
import com.suonk.notepad_plus.domain.note.id.SetCurrentNoteIdUseCase
import com.suonk.notepad_plus.domain.search.GetSearchNoteUseCase
import com.suonk.notepad_plus.domain.search.SetSearchNoteUseCase
import com.suonk.notepad_plus.domain.note.upsert.UpsertNoteUseCase
import com.suonk.notepad_plus.domain.sort.SetSortingParametersUseCase
import com.suonk.notepad_plus.firebase.user.CustomFirebaseUserRepository
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import com.suonk.notepad_plus.model.database.data.entities.NoteEntityWithPictures
import com.suonk.notepad_plus.ui.note.details.NoteDetailsViewState
import com.suonk.notepad_plus.ui.note.details.PictureViewState
import com.suonk.notepad_plus.utils.EquatableCallback
import com.suonk.notepad_plus.utils.NativeText
import com.suonk.notepad_plus.utils.Sorting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
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
    private val setCurrentNoteIdUseCase: SetCurrentNoteIdUseCase,
    private val customFirebaseUserRepository: CustomFirebaseUserRepository,
) : ViewModel() {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    private val _currentUserId = MutableStateFlow("")

    init {
        viewModelScope.launch {
            customFirebaseUserRepository.getCustomFirebaseUser().id?.let { userId ->
                _currentUserId.tryEmit(userId)
            }
        }
    }

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
        }.filter {
            filter.isMatching.invoke(it.noteEntity)
        }.sortedWith(sorting.comparator).map {
            transformEntityToViewState(it)
        }.toList()
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds), emptyList()
    )

    private fun transformEntityToViewState(entity: NoteEntityWithPictures) = NotesListViewState(
        id = entity.noteEntity.id,
        title = entity.noteEntity.title,
        content = entity.noteEntity.content,
        date = entity.noteEntity.date.format(dateTimeFormatter),
        color = entity.noteEntity.color.toARGB(),
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
            upsertNoteUseCase.invoke(entity, _currentUserId.value)
        }
    }

    fun setSearchParameters(search: String?) {
        setSearchNoteUseCase.invoke(search)
    }

    fun setCurrentFilter(filteringViewState: FilteringViewState) {
        when (filteringViewState) {
            FilteringViewState.REMOVE_FILTER -> {
                setFilterParametersUseCase.invoke(FilterEntity.REMOVE_FILTER)
            }

            FilteringViewState.ORANGE -> {
                setFilterParametersUseCase.invoke(FilterEntity.ORANGE)
            }

            FilteringViewState.PINK -> {
                setFilterParametersUseCase.invoke(FilterEntity.PINK)
            }

            FilteringViewState.GREEN -> {
                setFilterParametersUseCase.invoke(FilterEntity.GREEN)
            }

            FilteringViewState.YELLOW -> {
                setFilterParametersUseCase.invoke(FilterEntity.YELLOW)
            }

            FilteringViewState.PURPLE -> {
                setFilterParametersUseCase.invoke(FilterEntity.PURPLE)
            }

            FilteringViewState.BLUE -> {
                setFilterParametersUseCase.invoke(FilterEntity.BLUE)
            }

            FilteringViewState.RED -> {
                setFilterParametersUseCase.invoke(FilterEntity.RED)
            }

            else -> {
                setFilterParametersUseCase.invoke(FilterEntity.REMOVE_FILTER)
            }
        }
    }

    fun setCurrentSort(sortingViewState: SortingViewState) {
        when (sortingViewState) {
            SortingViewState.DATE_ASC -> {
                setSortingParametersUseCase.invoke(Sorting.DATE_ASC)
            }

            SortingViewState.DATE_DESC -> {
                setSortingParametersUseCase.invoke(Sorting.DATE_DESC)
            }

            SortingViewState.TITLE_ASC -> {
                setSortingParametersUseCase.invoke(Sorting.TITLE_ASC)
            }

            SortingViewState.TITLE_DESC -> {
                setSortingParametersUseCase.invoke(Sorting.TITLE_DESC)
            }

            SortingViewState.CONTENT_ASC -> {
                setSortingParametersUseCase.invoke(Sorting.CONTENT_ASC)
            }

            SortingViewState.CONTENT_DESC -> {
                setSortingParametersUseCase.invoke(Sorting.CONTENT_DESC)
            }

            SortingViewState.COLOR_ASC -> {
                setSortingParametersUseCase.invoke(Sorting.COLOR_ASC)
            }

            else -> {

            }
        }
    }
}