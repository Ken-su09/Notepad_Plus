package com.suonk.notepad_plus.ui.note.list

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.suonk.mynotepad.model.database.data.entities.NoteEntityWithPictures
import com.suonk.notepad_plus.domain.note.get.GetAllNotesFlowUseCase
import com.suonk.notepad_plus.domain.note.id.SetCurrentNoteIdUseCase
import com.suonk.notepad_plus.domain.note.search.GetSearchNoteUseCase
import com.suonk.notepad_plus.utils.CoroutineDispatcherProvider
import com.suonk.notepad_plus.utils.EquatableCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class NotesListViewModel @Inject constructor(
    private val getAllNotesFlowUseCase: GetAllNotesFlowUseCase,

    private val getSearchNoteUseCase: GetSearchNoteUseCase,
//    private val setSearchNoteUseCase: SetSearchNoteUseCase,

    private val setCurrentNoteIdUseCase: SetCurrentNoteIdUseCase,
    private val dispatcherProvider: CoroutineDispatcherProvider,
    private val application: Application,
    ) : ViewModel() {

    val notesListLiveData: LiveData<List<NotesListViewState>> = liveData(dispatcherProvider.io) {
        combine(getAllNotesFlowUseCase.invoke(), getSearchNoteUseCase.invoke()) { notes, search ->
            val list = notes.asSequence().map {
                transformEntityToViewState(it)
            }.filter {
                if (search != "" || search != " ") {
                    it.title.lowercase().contains(search.lowercase()) || it.content.lowercase().contains(search.lowercase())
                } else {
                    true
                }
            }.toList()
            emit(list)
        }.collect()
    }

    private fun transformEntityToViewState(entity: NoteEntityWithPictures): NotesListViewState {
        return NotesListViewState(
            id = entity.noteEntity.id,
            title = entity.noteEntity.title,
            content = entity.noteEntity.content,
            date = entity.noteEntity.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            onClickedCallback = EquatableCallback {
//                setCurrentNoteIdUseCase.invoke(entity.noteEntity.id)
            })
    }
}