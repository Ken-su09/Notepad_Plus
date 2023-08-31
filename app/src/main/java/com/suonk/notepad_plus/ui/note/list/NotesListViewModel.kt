package com.suonk.notepad_plus.ui.note.list

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.suonk.notepad_plus.domain.use_cases.note.get.GetAllNotesFlowUseCase
import com.suonk.notepad_plus.domain.use_cases.note.id.SetCurrentNoteIdUseCase
import com.suonk.notepad_plus.domain.use_cases.note.search.GetSearchNoteUseCase
import com.suonk.notepad_plus.model.database.data.entities.NoteEntityWithPictures
import com.suonk.notepad_plus.utils.CoroutineDispatcherProvider
import com.suonk.notepad_plus.utils.EquatableCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class NotesListViewModel @Inject constructor(
    private val getAllNotesFlowUseCase: GetAllNotesFlowUseCase,

    private val getSearchNoteUseCase: GetSearchNoteUseCase,
//    private val setSearchNoteUseCase: SetSearchNoteUseCase,

    private val setCurrentNoteIdUseCase: SetCurrentNoteIdUseCase,
    private val dispatcherProvider: CoroutineDispatcherProvider,
    private val application: Application,
) : ViewModel() {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    val notesListLiveData: LiveData<List<NotesListViewState>> = liveData(dispatcherProvider.io) {
        combine(
            getAllNotesFlowUseCase.invoke(),
            getSearchNoteUseCase.invoke(),
        ) { notes, search ->
            val list = notes.asSequence().filter {
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
            emit(list)
        }.collect()
    }

    private fun transformEntityToViewState(entity: NoteEntityWithPictures) = NotesListViewState(id = entity.noteEntity.id,
        title = entity.noteEntity.title,
        content = entity.noteEntity.content,
        date = entity.noteEntity.date.format(dateTimeFormatter),
        onClickedCallback = EquatableCallback {
            setCurrentNoteIdUseCase.invoke(entity.noteEntity.id)
        })
}