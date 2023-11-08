package com.suonk.notepad_plus.ui.note.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.domain.use_cases.note.get.GetNoteByIdFlowUseCase
import com.suonk.notepad_plus.domain.use_cases.note.id.GetCurrentIdFlowUseCase
import com.suonk.notepad_plus.domain.use_cases.note.id.SetCurrentNoteIdUseCase
import com.suonk.notepad_plus.domain.use_cases.note.upsert.UpsertNoteUseCase
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import com.suonk.notepad_plus.ui.note.details.actions_list.ActionsSealed
import com.suonk.notepad_plus.utils.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
    private val fixedClock: Clock,
) : ViewModel() {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    data class NoteDetailsForm(
        val id: Long,
        val title: String,
        val content: String,
        val color: Long,
    )

//    private val enabledTextStylesMutableStateFlow = MutableStateFlow(emptyList<ActionsSealed>())


    private val noteDetailsFormMutableSharedFlow = MutableSharedFlow<NoteDetailsForm>(replay = 1)
    val noteDetailsForm: Flow<NoteDetailsForm> = noteDetailsFormMutableSharedFlow.asSharedFlow()

    private val noteDetailsViewStateMutableStateFlow = MutableSharedFlow<NoteDetailsViewState>()

    private val noteDetailsDataEventMutableSharedFlow = MutableSharedFlow<NoteDetailsDataEvent>(extraBufferCapacity = 1)
    val noteDetailsDataEventFlow: Flow<NoteDetailsDataEvent> = noteDetailsDataEventMutableSharedFlow.asSharedFlow()

    private val _noteDetailsUiEvent = MutableSharedFlow<NoteDetailsUiEvent>()
    val noteDetailsUiEvent = _noteDetailsUiEvent.asSharedFlow()

    private val picturesMutableSharedFlow = MutableStateFlow<Set<PictureViewState>>(setOf())

    val noteDetailsFlow: StateFlow<NoteDetailsViewState> = combine(
        noteDetailsViewStateMutableStateFlow,
        noteDetailsFormMutableSharedFlow,
        picturesMutableSharedFlow,
//        enabledTextStylesMutableStateFlow
    ) { noteViewState, noteForm, pictures ->
        NoteDetailsViewState(
            id = noteViewState.id,
            title = noteViewState.title,
            content = noteViewState.content,
            color = noteViewState.color,
            dateText = noteViewState.dateText,
            dateValue = noteViewState.dateValue,

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
//            }
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds), NoteDetailsViewState(
            id = 0,
            title = "",
            content = "",
            color = listOfColors().random(),
            dateText = NativeText.Argument(R.string.last_update, ZonedDateTime.now(fixedClock).format(dateTimeFormatter)),
            dateValue = ZonedDateTime.now(fixedClock).toInstant(),
        )
    )

    private fun onItemClicked(actionSealed: ActionsSealed) {
//        enabledTextStylesMutableStateFlow.update { list ->
//            when (actionSealed.type) {
//                ActionType.REVERSE -> {
//                    noteDetailsEventMutableSharedFlow.tryEmit(NoteDetailsEvent.Revert(actionSealed.icon))
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
    }

    init {
        viewModelScope.launch {
            val id = getCurrentIdFlowUseCase.invoke().firstOrNull()

            Log.i("GetNoteDetails", "id : $id")

            val noteWithPictures = id?.let { getNoteByIdFlowUseCase.invoke(it).firstOrNull() }

            Log.i("GetNoteDetails", "noteWithPictures : $noteWithPictures")

            if (noteWithPictures == null) {
                noteDetailsViewStateMutableStateFlow.tryEmit(
                    NoteDetailsViewState(
                        id = 0,
                        title = "",
                        content = "",
                        color = listOfColors().random(),
                        dateText = NativeText.Simple(ZonedDateTime.now(fixedClock).format(dateTimeFormatter)),
                        dateValue = ZonedDateTime.now(fixedClock).toInstant(),
                    )
                )
                noteDetailsFormMutableSharedFlow.tryEmit(
                    NoteDetailsForm(
                        id = 0,
                        title = "",
                        content = "",
                        color = listOfColors().random()
                    )
                )
            } else {
                noteDetailsViewStateMutableStateFlow.emit(
                    NoteDetailsViewState(
                        id = noteWithPictures.noteEntity.id,
                        title = noteWithPictures.noteEntity.title,
                        content = noteWithPictures.noteEntity.content,
                        color = noteWithPictures.noteEntity.color,
                        dateText = NativeText.Argument(
                            R.string.last_update, noteWithPictures.noteEntity.date.format(dateTimeFormatter)
                        ),
                        dateValue = fromLocalDateToInstant(noteWithPictures.noteEntity.date),
                    )
                )
//                noteDetailsFormMutableSharedFlow.tryEmit(
//                    NoteDetailsForm(
//                        id = noteWithPictures.noteEntity.id,
//                        title = noteWithPictures.noteEntity.title,
//                        content = noteWithPictures.noteEntity.content,
//                        color = noteWithPictures.noteEntity.color,
//                    )
//                )
            }
        }
    }

    fun onSaveNoteMenuItemClicked(title: String, content: String, updatedColor: Long) {

    }

    fun onDeleteNoteMenuItemClicked() {
        viewModelScope.launch {
            getCurrentIdFlowUseCase.invoke().collect { id ->
                val lastUpdateDate = ZonedDateTime.now(fixedClock).toInstant()

//                noteDetailsViewStateMutableSharedFlow.firstOrNull()?.let {
//                    upsertNoteUseCase.invoke(
//                        NoteEntity(
//                            id = id ?: 0L,
//                            title = it.title,
//                            content = it.content,
//                            date = fromInstantToLocalDate(lastUpdateDate),
//                            color = 0,
//                            isFavorite = false,
//                            isDeleted = true
//                        )
//                    )
//                }

//                noteDetailsDataEventMutableSharedFlow.s
            }
        }
    }
//
//    fun updateEditTextContent(content: String) {
//        noteDetailsViewStateMutableSharedFlow.tryEmit() {
//            content
//        }
//    }
//
//    fun onColorSelected(content: String) {
//        noteDetailsViewStateMutableSharedFlow.tryEmit() {
//            content
//        }
//    }

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

            }

            is NoteDetailsDataEvent.ChangeContent -> {

            }

            is NoteDetailsDataEvent.ChangeColor -> {

            }

            is NoteDetailsDataEvent.SaveDataNote -> {
                viewModelScope.launch {
                    if (isEmptyOrBlank(event.title) || isEmptyOrBlank(event.content)) {
                        _noteDetailsUiEvent.emit(NoteDetailsUiEvent.ShowToast(NativeText.Resource(R.string.field_empty_toast_msg)))
                    } else {
                        val lastUpdateDate = ZonedDateTime.now(fixedClock).toInstant()

//                        upsertNoteUseCase.invoke(
//                            NoteEntity(
//                                id = note.id,
//                                title = note.title,
//                                content = note.content,
//                                date = fromInstantToLocalDate(lastUpdateDate),
//                                color = note.color,
//                                isFavorite = false,
//                                isDeleted = false
//                            )
//                        )

                        _noteDetailsUiEvent.emit(NoteDetailsUiEvent.ActionFinish)
                    }
                }
            }

            else -> {

            }
        }
    }

    fun listOfColors() = listOf(0xFFffab91, 0xFFe8ed9d, 0xFFd095db, 0xFF7fdeea, 0xFFf48fb1)
}