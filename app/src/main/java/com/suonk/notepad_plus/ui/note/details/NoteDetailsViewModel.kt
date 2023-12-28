package com.suonk.notepad_plus.ui.note.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.designsystem.utils.ColorEntity
import com.suonk.notepad_plus.domain.note.get_note.GetNoteByIdFlowUseCase
import com.suonk.notepad_plus.domain.note.id.GetCurrentIdFlowUseCase
import com.suonk.notepad_plus.domain.note.id.SetCurrentNoteIdUseCase
import com.suonk.notepad_plus.domain.note.upsert.UpsertNoteUseCase
import com.suonk.notepad_plus.firebase.user.CustomFirebaseUserRepository
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import com.suonk.notepad_plus.ui.note.details.actions_list.ActionsSealed
import com.suonk.notepad_plus.utils.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
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
    private val getCurrentIdFlowUseCase: GetCurrentIdFlowUseCase,
    private val setCurrentNoteIdUseCase: SetCurrentNoteIdUseCase,
    private val upsertNoteUseCase: UpsertNoteUseCase,
    private val customFirebaseUserRepository: CustomFirebaseUserRepository,
    private val fixedClock: Clock
) : ViewModel() {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    private var noteDetailsViewStateMutableSharedFlow = MutableSharedFlow<NoteDetailsViewState>(replay = 1)
    private var picturesMutableSharedFlow = MutableStateFlow<Set<PictureViewState>>(setOf())

    private val _noteTitle = MutableStateFlow(NativeText.SimpleOrResource("", R.string.enter_a_title))
    val noteTitle = _noteTitle.asStateFlow()

    private val _noteContent = MutableStateFlow(NativeText.SimpleOrResource("", R.string.enter_some_content))
    val noteContent = _noteContent.asStateFlow()

    private val _noteColor = MutableStateFlow(ColorEntity.values().random())
    val noteColor = _noteColor.asStateFlow()

    private val _noteAllColors = MutableStateFlow(ColorEntity.values())
    val noteAllColors = _noteAllColors.asStateFlow()

    private val _isDeleted = MutableStateFlow(false)
    val isDeleted = _isDeleted.asStateFlow()

    private val _currentUserId = MutableStateFlow("")

    private val enabledTextStylesMutableStateFlow = MutableStateFlow(emptyList<ActionsSealed>())

    private val _noteDetailsUiEvent = MutableSharedFlow<NoteDetailsUiEvent>()
    val noteDetailsUiEvent = _noteDetailsUiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            customFirebaseUserRepository.getCustomFirebaseUser().id?.let { userId ->
                Log.i("GetUserId", "NoteDetailsViewModel : userId : ${userId}")
                _currentUserId.tryEmit(userId)
            }
            getCurrentIdFlowUseCase.invoke().collect { id ->
                val noteWithPictures = id?.let {
                    getNoteByIdFlowUseCase.invoke(it).firstOrNull()
                }
                if (noteWithPictures == null) {
                    noteDetailsViewStateMutableSharedFlow.tryEmit(
                        NoteDetailsViewState(
                            id = 0,
                            title = "",
                            content = "",
                            color = ColorEntity.values().random(),
                            dateText = NativeText.Argument(R.string.last_update, ZonedDateTime.now(fixedClock).format(dateTimeFormatter)),
                            dateValue = ZonedDateTime.now(fixedClock).toInstant(),
                            actions = listOf()
                        )
                    )
                } else {
                    _noteTitle.value = NativeText.SimpleOrResource(noteWithPictures.noteEntity.title, R.string.enter_a_title)
                    _noteContent.value = NativeText.SimpleOrResource(noteWithPictures.noteEntity.content, R.string.enter_some_content)
                    _noteColor.value = noteWithPictures.noteEntity.color
                    _isDeleted.value = noteWithPictures.noteEntity.isDeleted

                    noteDetailsViewStateMutableSharedFlow.tryEmit(
                        NoteDetailsViewState(
                            id = noteWithPictures.noteEntity.id,
                            title = noteWithPictures.noteEntity.title,
                            content = noteWithPictures.noteEntity.content,
                            color = noteWithPictures.noteEntity.color,
                            dateText = NativeText.Argument(
                                R.string.last_update, noteWithPictures.noteEntity.date.format(dateTimeFormatter)
                            ),
                            dateValue = fromLocalDateToInstant(noteWithPictures.noteEntity.date),
                            actions = listOf()
                        )
                    )
                    picturesMutableSharedFlow.tryEmit(noteWithPictures.photos.map {
                        PictureViewState(it.picture)
                    }.toSet())
                }
            }
        }
    }

    fun setNoteIdToNull() {
        setCurrentNoteIdUseCase.invoke(-1)
    }

    private fun fromLocalDateToInstant(value: LocalDateTime): Instant = value.toInstant(ZoneOffset.UTC)
    private fun fromInstantToLocalDate(instant: Instant): LocalDateTime = instant.atZone(ZoneOffset.UTC).toLocalDateTime()

    private fun isEmptyOrBlank(value: String): Boolean {
        return value.isEmpty() || value.isBlank() || value == "" || value == " "
    }

    fun onDataEvent(event: NoteDetailsDataEvent) {
        when (event) {
            is NoteDetailsDataEvent.ChangeTitle -> {
                _noteTitle.value = NativeText.SimpleOrResource(event.title, R.string.enter_a_title)
            }

            is NoteDetailsDataEvent.ChangeContent -> {
                _noteContent.value = NativeText.SimpleOrResource(event.content, R.string.enter_some_content)
            }

            is NoteDetailsDataEvent.ChangeColor -> {
                _noteColor.value = event.colorEntity
            }

            is NoteDetailsDataEvent.SaveNote -> {
                viewModelScope.launch {
                    if (isEmptyOrBlank(_noteTitle.value.text) || isEmptyOrBlank(_noteContent.value.text)) {
                        _noteDetailsUiEvent.emit(NoteDetailsUiEvent.ShowToast(NativeText.Resource(R.string.field_empty_toast_msg)))
                    } else {
                        val lastUpdateDate = ZonedDateTime.now(fixedClock).toInstant()

                        noteDetailsViewStateMutableSharedFlow.firstOrNull()?.let {
                            upsertNoteUseCase.invoke(
                                NoteEntity(
                                    id = it.id,
                                    title = _noteTitle.value.text,
                                    content = _noteContent.value.text,
                                    date = fromInstantToLocalDate(lastUpdateDate),
                                    color = _noteColor.value,
                                    isFavorite = false,
                                    isDeleted = false
                                ), _currentUserId.value
                            )
                        }

                        _noteDetailsUiEvent.emit(NoteDetailsUiEvent.ActionFinish)
                    }
                }
            }

            is NoteDetailsDataEvent.DeleteNote -> {
                viewModelScope.launch {
                    val lastUpdateDate = ZonedDateTime.now(fixedClock).toInstant()

                    noteDetailsViewStateMutableSharedFlow.firstOrNull()?.let { noteDetails ->
                        upsertNoteUseCase.invoke(
                            NoteEntity(
                                id = noteDetails.id,
                                title = noteDetails.title,
                                content = noteDetails.content,
                                date = fromInstantToLocalDate(lastUpdateDate),
                                color = noteDetails.color,
                                isFavorite = false,
                                isDeleted = !isDeleted.value
                            ), _currentUserId.value
                        )
                    }

                    _noteDetailsUiEvent.emit(NoteDetailsUiEvent.ActionFinish)
                }
            }

            NoteDetailsDataEvent.DefinitiveDeleteNote -> {

            }

            is NoteDetailsDataEvent.RestoreNote -> {
                viewModelScope.launch {
                    val lastUpdateDate = ZonedDateTime.now(fixedClock).toInstant()

                    noteDetailsViewStateMutableSharedFlow.firstOrNull()?.let { noteDetails ->
                        upsertNoteUseCase.invoke(
                            NoteEntity(
                                id = noteDetails.id,
                                title = noteDetails.title,
                                content = noteDetails.content,
                                date = fromInstantToLocalDate(lastUpdateDate),
                                color = noteDetails.color,
                                isFavorite = false,
                                isDeleted = !isDeleted.value
                            ), _currentUserId.value
                        )
                    }

                    _noteDetailsUiEvent.emit(NoteDetailsUiEvent.ActionFinish)
                }
            }
        }
    }
}