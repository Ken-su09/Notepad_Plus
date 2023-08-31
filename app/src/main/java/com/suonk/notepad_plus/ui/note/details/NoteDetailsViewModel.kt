package com.suonk.notepad_plus.ui.note.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.domain.use_cases.note.get.GetNoteByIdFlowUseCase
import com.suonk.notepad_plus.domain.use_cases.note.id.GetCurrentIdFlowChannelUseCase
import com.suonk.notepad_plus.domain.use_cases.note.upsert.UpsertNoteUseCase
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import com.suonk.notepad_plus.utils.CoroutineDispatcherProvider
import com.suonk.notepad_plus.utils.NativeText
import com.suonk.notepad_plus.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class NoteDetailsViewModel @Inject constructor(
    private val getNoteByIdFlowUseCase: GetNoteByIdFlowUseCase,
    private val getCurrentIdFlowChannelUseCase: GetCurrentIdFlowChannelUseCase,
    private val upsertNoteUseCase: UpsertNoteUseCase,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val fixedClock: Clock
) : ViewModel() {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    private var noteDetailsViewStateMutableSharedFlow = MutableSharedFlow<NoteDetailsViewState>(replay = 1)
    private var picturesMutableSharedFlow = MutableStateFlow<Set<PictureViewState>>(setOf())

    val finishSavingSingleLiveEvent = SingleLiveEvent<Unit>()
    var toastMessageSingleLiveEvent = SingleLiveEvent<NativeText>()

    val noteDetailsViewState: LiveData<NoteDetailsViewState> = liveData(coroutineDispatcherProvider.io) {
        combine(noteDetailsViewStateMutableSharedFlow, picturesMutableSharedFlow) { note, pictures ->
            emit(
                NoteDetailsViewState(
                    id = note.id,
                    title = note.title,
                    content = note.content,
                    dateText = note.dateText,
                    dateValue = note.dateValue
                )
            )
        }.collect()
    }

    init {
        viewModelScope.launch(coroutineDispatcherProvider.io) {
            getCurrentIdFlowChannelUseCase.invoke().collect { id ->
                Log.i("GetNote", "id : $id")
                val noteWithPictures = id?.let { getNoteByIdFlowUseCase.invoke(it).firstOrNull() }
                if (noteWithPictures == null) {
                    noteDetailsViewStateMutableSharedFlow.tryEmit(
                        NoteDetailsViewState(
                            id = 0,
                            title = "",
                            content = "",
                            dateText = NativeText.Argument(R.string.last_update, ZonedDateTime.now(fixedClock).format(dateTimeFormatter)),
                            dateValue = ZonedDateTime.now(fixedClock).toInstant(),
                        )
                    )
                } else {
                    noteDetailsViewStateMutableSharedFlow.tryEmit(
                        NoteDetailsViewState(
                            id = noteWithPictures.noteEntity.id,
                            title = noteWithPictures.noteEntity.title,
                            content = noteWithPictures.noteEntity.content,
                            dateText = NativeText.Argument(
                                R.string.last_update,
                                noteWithPictures.noteEntity.date.format(dateTimeFormatter)
                            ),
                            dateValue = fromLocalDateToInstant(noteWithPictures.noteEntity.date),
                        )
                    )
                    picturesMutableSharedFlow.tryEmit(noteWithPictures.photos.map {
                        PictureViewState(it.picture)
                    }.toSet())
                }
            }
        }
    }

    fun onSaveNoteMenuItemClicked(title: String, content: String) {
        viewModelScope.launch(coroutineDispatcherProvider.io) {
            getCurrentIdFlowChannelUseCase.invoke().collect { id ->
                if (isEmptyOrBlank(title) || isEmptyOrBlank(content)) {
                    withContext(coroutineDispatcherProvider.main) {
                        toastMessageSingleLiveEvent.setValue(NativeText.Resource(R.string.field_empty_toast_msg))
                    }
                } else {
                    val lastUpdateDate =
                        noteDetailsViewStateMutableSharedFlow.first().dateValue ?: ZonedDateTime.now(fixedClock).toInstant()

                    upsertNoteUseCase.invoke(
                        NoteEntity(
                            id = id ?: 0L,
                            title = title,
                            content = content,
                            date = fromInstantToLocalDate(lastUpdateDate),
                            color = 0,
                            isFavorite = false,
                            isDeleted = false
                        )
                    )

                    withContext(coroutineDispatcherProvider.main) {
                        finishSavingSingleLiveEvent.setValue(Unit)
                    }
                }
            }
        }
    }

    private fun fromLocalDateToInstant(value: LocalDateTime): Instant = value.toInstant(ZoneOffset.UTC)
    private fun fromInstantToLocalDate(instant: Instant): LocalDateTime = instant.atZone(ZoneOffset.UTC).toLocalDateTime()

    private fun isEmptyOrBlank(value: String): Boolean {
        return value.isEmpty() || value.isBlank() || value == "" || value == " "
    }
}