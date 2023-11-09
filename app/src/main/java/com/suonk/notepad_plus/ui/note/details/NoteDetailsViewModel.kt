package com.suonk.notepad_plus.ui.note.details

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.domain.use_cases.note.get.GetNoteByIdFlowUseCase
import com.suonk.notepad_plus.domain.use_cases.note.id.GetCurrentIdFlowUseCase
import com.suonk.notepad_plus.domain.use_cases.note.id.SetCurrentNoteIdUseCase
import com.suonk.notepad_plus.domain.use_cases.note.upsert.UpsertNoteUseCase
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import com.suonk.notepad_plus.ui.note.details.actions_list.ActionType
import com.suonk.notepad_plus.ui.note.details.actions_list.ActionsSealed
import com.suonk.notepad_plus.ui.note.details.actions_list.EditTextAction
import com.suonk.notepad_plus.utils.CoroutineDispatcherProvider
import com.suonk.notepad_plus.utils.EquatableCallback
import com.suonk.notepad_plus.utils.NativeText
import com.suonk.notepad_plus.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class NoteDetailsViewModel @Inject constructor(
    private val getNoteByIdFlowUseCase: GetNoteByIdFlowUseCase,
    private val getCurrentIdFlowUseCase: GetCurrentIdFlowUseCase,
    private val setCurrentNoteIdUseCase: SetCurrentNoteIdUseCase,
    private val upsertNoteUseCase: UpsertNoteUseCase,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val fixedClock: Clock,
) : ViewModel() {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    private var noteDetailsViewStateMutableSharedFlow = MutableSharedFlow<NoteDetailsViewState>(replay = 1)
    private var picturesMutableSharedFlow = MutableStateFlow<Set<PictureViewState>>(setOf())

    private val _noteTitle = MutableStateFlow("Enter title")
    val noteTitle = _noteTitle.asStateFlow()

    private val _noteContent = MutableStateFlow("Enter content")
    val noteContent = _noteContent.asStateFlow()

    private val _noteColor = MutableStateFlow(listOfColors().random())
    val noteColor = _noteColor.asStateFlow()

    private val _isDeleted = MutableStateFlow(false)
    val isDeleted = _isDeleted.asStateFlow()

    private val enabledTextStylesMutableStateFlow = MutableStateFlow(emptyList<ActionsSealed>())

    private val _noteDetailsUiEvent = MutableSharedFlow<NoteDetailsUiEvent>()
    val noteDetailsUiEvent = _noteDetailsUiEvent.asSharedFlow()

//    val noteDetailsFlow: StateFlow<NoteDetailsViewState> = combine(
//        noteDetailsViewStateMutableSharedFlow, picturesMutableSharedFlow, enabledTextStylesMutableStateFlow, contentFlow
//    ) { note, pictures, enabledTextStyles, content ->
//        NoteDetailsViewState(id = note.id,
//            title = note.title,
//            content = if (content == "") note.content else content,
//            color = note.color,
//            dateText = note.dateText,
//            dateValue = note.dateValue,
//            actions = ActionsSealed.values().map { availableTextStyle ->
//                val isEnabled = enabledTextStyles.contains(availableTextStyle)
//
//                EditTextAction(textStyle = availableTextStyle,
//                    icon = availableTextStyle.icon,
//                    background = availableTextStyle.background,
//                    enabled = isEnabled,
//                    onClickedCallback = EquatableCallback {
//                        onItemClicked(availableTextStyle)
//                    })
//            })
//    }.stateIn(
//        viewModelScope, SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds), NoteDetailsViewState(
//            id = 0,
//            title = "",
//            content = "",
//            color = listOfColors().random(),
//            dateText = NativeText.Argument(R.string.last_update, ZonedDateTime.now(fixedClock).format(dateTimeFormatter)),
//            dateValue = ZonedDateTime.now(fixedClock).toInstant(),
//            actions = listOf()
//        )
//    )

//    private fun onItemClicked(actionSealed: ActionsSealed) {
//        enabledTextStylesMutableStateFlow.update { list ->
//            when (actionSealed.type) {
//                ActionType.REVERSE -> {
//                    editorActionsSingleLiveEvent.setValue(actionSealed.icon)
//                    list
//                }
//
//                ActionType.TEXT_STYLE -> {
//                    editorActionsSingleLiveEvent.setValue(actionSealed.icon)
//                    if (list.contains(actionSealed)) {
//                        actionSealed.background = R.drawable.custom_item_background
//                        list - actionSealed
//                    } else {
//                        actionSealed.background = R.drawable.custom_item_background_pressed
//                        list + actionSealed
//                    }
//                }
//
//                ActionType.X2 -> {
//                    list
//                }
//
//                ActionType.HEADING -> {
//                    var currentList = emptyList<ActionsSealed>()
//                    val headingItem = list.find { it.type == ActionType.HEADING }
//
//                    if (headingItem != null) {
//                        currentList = list - headingItem
//                    }
//
//                    headingItem?.background = R.drawable.custom_item_background
//
//                    if (list.contains(actionSealed)) {
//                        editorActionsSingleLiveEvent.setValue(actionSealed.icon)
//                        actionSealed.background = R.drawable.custom_item_background
//                        currentList - actionSealed
//                    } else {
//                        editorActionsSingleLiveEvent.setValue(actionSealed.icon)
//                        actionSealed.background = R.drawable.custom_item_background_pressed
//                        currentList + actionSealed
//                    }
//                }
//
//                ActionType.COLOR -> {
//                    list
//                }
//
//                ActionType.INSERT -> {
//                    list
//                }
//
//                ActionType.ALIGN -> {
//                    var currentList = emptyList<ActionsSealed>()
//                    val headingItem = list.find { it.type == ActionType.ALIGN }
//
//                    if (headingItem != null) {
//                        currentList = list - headingItem
//                    }
//
//                    headingItem?.background = R.drawable.custom_item_background
//
//                    if (list.contains(actionSealed)) {
//                        editorActionsSingleLiveEvent.setValue(actionSealed.icon)
//                        actionSealed.background = R.drawable.custom_item_background
//                        currentList - actionSealed
//                    } else {
//                        editorActionsSingleLiveEvent.setValue(actionSealed.icon)
//                        actionSealed.background = R.drawable.custom_item_background_pressed
//                        currentList + actionSealed
//                    }
//                }
//            }
//        }
//    }

    init {
        viewModelScope.launch(coroutineDispatcherProvider.io) {
            getCurrentIdFlowUseCase.invoke().collect { id ->
                val noteWithPictures = id?.let { getNoteByIdFlowUseCase.invoke(it).firstOrNull() }
                if (noteWithPictures == null) {
                    noteDetailsViewStateMutableSharedFlow.tryEmit(
                        NoteDetailsViewState(
                            id = 0,
                            title = "",
                            content = "",
                            color = listOfColors().random(),
                            dateText = NativeText.Argument(R.string.last_update, ZonedDateTime.now(fixedClock).format(dateTimeFormatter)),
                            dateValue = ZonedDateTime.now(fixedClock).toInstant(),
                            actions = listOf()
                        )
                    )
                } else {
                    try {
                        _noteTitle.value = noteWithPictures.noteEntity.title
                        _noteContent.value = noteWithPictures.noteEntity.content
                        _noteColor.value = noteWithPictures.noteEntity.color

                        Log.i("GetNoteDetails", "noteWithPictures.noteEntity.color : ${noteWithPictures.noteEntity.color}")

                        _isDeleted.value = noteWithPictures.noteEntity.isDeleted
                    } catch (e: Exception) {
                        Log.i("GetNoteDetails", "e : $e")
                    }

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

    fun onDeleteNoteMenuItemClicked() {
        viewModelScope.launch(coroutineDispatcherProvider.io) {
            getCurrentIdFlowUseCase.invoke().collect { id ->
                val lastUpdateDate = ZonedDateTime.now(fixedClock).toInstant()

                noteDetailsViewStateMutableSharedFlow.firstOrNull()?.let {
                    upsertNoteUseCase.invoke(
                        NoteEntity(
                            id = id ?: 0L,
                            title = it.title,
                            content = it.content,
                            date = fromInstantToLocalDate(lastUpdateDate),
                            color = 0,
                            isFavorite = false,
                            isDeleted = true
                        )
                    )
                }

                withContext(coroutineDispatcherProvider.main) {
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

    fun onEvent(event: NoteDetailsDataEvent) {
        when (event) {
            is NoteDetailsDataEvent.ChangeTitle -> {
                _noteTitle.value = event.title
            }

            is NoteDetailsDataEvent.ChangeContent -> {
                _noteContent.value = event.content
            }

            is NoteDetailsDataEvent.ChangeColor -> {
                _noteColor.value = event.color
            }

            is NoteDetailsDataEvent.SaveNote -> {
                viewModelScope.launch {
                    if (isEmptyOrBlank(_noteTitle.value) || isEmptyOrBlank(_noteContent.value)) {
                        _noteDetailsUiEvent.emit(NoteDetailsUiEvent.ShowToast(NativeText.Resource(R.string.field_empty_toast_msg)))
                    } else {
                        val lastUpdateDate = ZonedDateTime.now(fixedClock).toInstant()

                        Log.i("GetNoteDetails", "_noteTitle.value : ${_noteTitle.value}")
                        Log.i("GetNoteDetails", "_noteContent.value : ${_noteContent.value}")

                        noteDetailsViewStateMutableSharedFlow.firstOrNull()?.let {
                            upsertNoteUseCase.invoke(
                                NoteEntity(
                                    id = it.id,
                                    title = _noteTitle.value,
                                    content = _noteContent.value,
                                    date = fromInstantToLocalDate(lastUpdateDate),
                                    color = _noteColor.value,
                                    isFavorite = false,
                                    isDeleted = false
                                )
                            )
                        }

                        _noteDetailsUiEvent.emit(NoteDetailsUiEvent.ActionFinish)
                    }
                }
            }

            is NoteDetailsDataEvent.DeleteRestoreNote -> {
                viewModelScope.launch {
                    noteDetailsViewStateMutableSharedFlow.firstOrNull()?.let { noteDetails ->
                        noteDetails.dateValue?.let {
                            upsertNoteUseCase.invoke(
                                NoteEntity(
                                    id = noteDetails.id,
                                    title = noteDetails.title,
                                    content = noteDetails.content,
                                    date = fromInstantToLocalDate(it),
                                    color = noteDetails.color,
                                    isFavorite = false,
                                    isDeleted = isDeleted.value
                                )
                            )
                        }
                    }

                    _noteDetailsUiEvent.emit(NoteDetailsUiEvent.ActionFinish)
                }
            }

            else -> {

            }
        }
    }

    fun listOfColors() = listOf(0xFFffab91, 0xFFe8ed9d, 0xFFd095db, 0xFF7fdeea, 0xFFf48fb1)
}