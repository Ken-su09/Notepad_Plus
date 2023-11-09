package com.suonk.notepad_plus.ui.note.deleted_list

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.suonk.notepad_plus.domain.use_cases.note.get.GetAllDeletedNotesFlowUseCase
import com.suonk.notepad_plus.domain.use_cases.note.get.GetAllNotesFlowUseCase
import com.suonk.notepad_plus.domain.use_cases.note.id.SetCurrentNoteIdUseCase
import com.suonk.notepad_plus.domain.use_cases.note.search.GetSearchNoteUseCase
import com.suonk.notepad_plus.domain.use_cases.note.search.SetSearchNoteUseCase
import com.suonk.notepad_plus.domain.use_cases.note.upsert.UpsertNoteUseCase
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import com.suonk.notepad_plus.model.database.data.entities.NoteEntityWithPictures
import com.suonk.notepad_plus.ui.note.list.NotesListViewState
import com.suonk.notepad_plus.utils.CoroutineDispatcherProvider
import com.suonk.notepad_plus.utils.EquatableCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class DeletedNotesListViewModel @Inject constructor(
    private val getAllDeletedNotesFlowUseCase: GetAllDeletedNotesFlowUseCase,

    private val getSearchNoteUseCase: GetSearchNoteUseCase,
    private val setSearchNoteUseCase: SetSearchNoteUseCase,

    private val upsertNoteUseCase: UpsertNoteUseCase,

    private val setCurrentNoteIdUseCase: SetCurrentNoteIdUseCase,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) : ViewModel() {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    val deletedNotesListFlow: StateFlow<List<DeletedNoteListViewState>> = combine(
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


    private fun transformEntityToViewState(entity: NoteEntityWithPictures) = DeletedNoteListViewState(id = entity.noteEntity.id,
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
        viewModelScope.launch(dispatcherProvider.io) {
            upsertNoteUseCase.invoke(
                NoteEntity(
                    id = entity.noteEntity.id,
                    title = entity.noteEntity.title,
                    content = entity.noteEntity.content,
                    date = entity.noteEntity.date,
                    color = entity.noteEntity.color,
                    isFavorite = entity.noteEntity.isFavorite,
                    isDeleted = false,
                )
            )
        }
    }

    fun setSearchParameters(search: String?) {
        setSearchNoteUseCase.invoke(search)
    }
}