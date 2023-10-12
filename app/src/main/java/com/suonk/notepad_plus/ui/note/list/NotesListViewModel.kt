package com.suonk.notepad_plus.ui.note.list

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.suonk.notepad_plus.domain.use_cases.note.get.GetAllNotesFlowUseCase
import com.suonk.notepad_plus.domain.use_cases.note.id.SetCurrentNoteIdUseCase
import com.suonk.notepad_plus.domain.use_cases.note.search.GetSearchNoteUseCase
import com.suonk.notepad_plus.domain.use_cases.note.search.SetSearchNoteUseCase
import com.suonk.notepad_plus.model.database.data.entities.NoteEntityWithPictures
import com.suonk.notepad_plus.utils.CoroutineDispatcherProvider
import com.suonk.notepad_plus.utils.EquatableCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class NotesListViewModel @Inject constructor(
    private val getAllNotesFlowUseCase: GetAllNotesFlowUseCase,

    private val getSearchNoteUseCase: GetSearchNoteUseCase,
    private val setSearchNoteUseCase: SetSearchNoteUseCase,

    private val setCurrentNoteIdUseCase: SetCurrentNoteIdUseCase,
    private val dispatcherProvider: CoroutineDispatcherProvider,
    private val application: Application,
) : ViewModel() {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    val notesListFlow: StateFlow<List<NotesListViewState>> = combine(
        getAllNotesFlowUseCase.invoke(),
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
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds), emptyList()
    )


    private fun transformEntityToViewState(entity: NoteEntityWithPictures) = NotesListViewState(id = entity.noteEntity.id,
        title = entity.noteEntity.title,
        content = entity.noteEntity.content,
        date = entity.noteEntity.date.format(dateTimeFormatter),
        onClickedCallback = EquatableCallback {
            setCurrentNoteIdUseCase.invoke(entity.noteEntity.id)
        })

    fun onNewNoteClicked() {
        setCurrentNoteIdUseCase.invoke(-1)
    }

    fun setSearchParameters(search: String?){
        setSearchNoteUseCase.invoke(search)
    }
}