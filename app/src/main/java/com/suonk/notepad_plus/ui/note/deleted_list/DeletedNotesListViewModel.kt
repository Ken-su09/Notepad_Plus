package com.suonk.notepad_plus.ui.note.deleted_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suonk.notepad_plus.domain.note.get_note.GetAllDeletedNotesFlowUseCase
import com.suonk.notepad_plus.domain.note.id.SetCurrentNoteIdUseCase
import com.suonk.notepad_plus.domain.search.GetSearchNoteUseCase
import com.suonk.notepad_plus.domain.search.SetSearchNoteUseCase
import com.suonk.notepad_plus.domain.note.upsert.UpsertNoteUseCase
import com.suonk.notepad_plus.firebase.user.CustomFirebaseUserRepository
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
class DeletedNotesListViewModel @Inject constructor(
    getAllDeletedNotesFlowUseCase: GetAllDeletedNotesFlowUseCase,
    getSearchNoteUseCase: GetSearchNoteUseCase,
    private val setSearchNoteUseCase: SetSearchNoteUseCase,

    private val upsertNoteUseCase: UpsertNoteUseCase,

    private val setCurrentNoteIdUseCase: SetCurrentNoteIdUseCase,
    private val customFirebaseUserRepository: CustomFirebaseUserRepository,
) : ViewModel() {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    private val _searchBarText = MutableStateFlow("")
    val searchBarText = _searchBarText.asStateFlow()

    private val _currentUserId = MutableStateFlow("")

    val deletedNotesListFlow: StateFlow<List<DeletedNotesListViewState>> = combine(
        getAllDeletedNotesFlowUseCase.invoke(),
        getSearchNoteUseCase.invoke(),
    ) { notes, search ->
        notes.asSequence().filter {
            if (search != null) {
                it.noteEntity.title.contains(search, ignoreCase = true) || it.noteEntity.content.contains(
                    search, ignoreCase = true
                )
            } else {
                true
            }
        }.map {
            transformEntityToViewState(it)
        }.toList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds), emptyList())

    init {
        viewModelScope.launch {
            customFirebaseUserRepository.getCustomFirebaseUser().id?.let { userId ->
                _currentUserId.tryEmit(userId)
            }
        }
    }

    private fun transformEntityToViewState(entity: NoteEntityWithPictures) = DeletedNotesListViewState(id = entity.noteEntity.id,
        title = entity.noteEntity.title,
        content = entity.noteEntity.content,
        date = entity.noteEntity.date.format(dateTimeFormatter),
        color = entity.noteEntity.color,
        onItemNoteClicked = EquatableCallback {
            setCurrentNoteIdUseCase.invoke(entity.noteEntity.id)
        },
        onRestoreNoteClicked = EquatableCallback {
            onRestoreNoteMenuItemClicked(entity)
        })

    private fun onRestoreNoteMenuItemClicked(entity: NoteEntityWithPictures) {
        viewModelScope.launch {
            upsertNoteUseCase.invoke(
                NoteEntity(
                    id = entity.noteEntity.id,
                    title = entity.noteEntity.title,
                    content = entity.noteEntity.content,
                    date = entity.noteEntity.date,
                    color = entity.noteEntity.color,
                    isFavorite = entity.noteEntity.isFavorite,
                    isDeleted = false,
                ), _currentUserId.value
            )
        }
    }

    fun setSearchParameters(search: String?) {
        search?.let {
            _searchBarText.value = it
        }
        setSearchNoteUseCase.invoke(search)
    }
}