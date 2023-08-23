package com.suonk.notepad_plus.ui.note.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.suonk.notepad_plus.domain.note.get.GetAllNotesFlowUseCase
import com.suonk.notepad_plus.domain.note.id.SetCurrentNoteIdUseCase
import com.suonk.notepad_plus.domain.note.search.GetSearchNoteUseCase
import com.suonk.notepad_plus.utils.CoroutineDispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class NotesListViewModel @Inject constructor(
    private val getAllNotesFlowUseCase: GetAllNotesFlowUseCase,

    private val getSearchNoteUseCase: GetSearchNoteUseCase,
//    private val setSearchNoteUseCase: SetSearchNoteUseCase,

    private val setCurrentNoteIdUseCase: SetCurrentNoteIdUseCase,
    private val dispatcherProvider: CoroutineDispatcherProvider,

    ) : ViewModel() {

    val notesListLiveData: LiveData<List<NotesListViewState>> = liveData(dispatcherProvider.io) {
        combine(getAllNotesFlowUseCase.invoke(), getSearchNoteUseCase.invoke()) { notes, search ->

            emit(listOf())
        }.collect()
    }

}