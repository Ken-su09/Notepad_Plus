package com.suonk.notepad_plus.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.suonk.notepad_plus.domain.use_cases.note.id.GetCurrentIdFlowChannelUseCase
import com.suonk.notepad_plus.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val getCurrentIdFlowChannelUseCase: GetCurrentIdFlowChannelUseCase) : ViewModel() {

    val mainViewAction: LiveData<Event<MainViewAction>> = liveData {
        getCurrentIdFlowChannelUseCase.invoke().collect { id ->
            Log.i("GetNotesList", "MainViewModel - id : $id")
            id?.let {
                emit(Event(MainViewAction.Navigate.Detail(id)))
            }
        }
    }
}