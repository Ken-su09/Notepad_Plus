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
import com.suonk.notepad_plus.ui.note.details.actions_list.ActionType
import com.suonk.notepad_plus.ui.note.details.actions_list.ActionsSealed
import com.suonk.notepad_plus.ui.note.details.actions_list.EditTextAction
import com.suonk.notepad_plus.utils.CoroutineDispatcherProvider
import com.suonk.notepad_plus.utils.EquatableCallback
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
    private val fixedClock: Clock,
) : ViewModel() {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

//    private val _noteTitle = mutableStateOf("Enter title...")
//    val noteTitle: State<String> = _noteTitle
//
//    private val _noteContent = mutableStateOf("Enter some content")
//    val noteContent: State<String> = _noteContent
//
//    private val _noteColor = mutableStateOf(listOfColors().random())
//    val noteColor: State<Long> = _noteColor

    private val noteDetailsViewStateMutableSharedFlow = MutableSharedFlow<NoteDetailsForm>(replay = 1)

    private data class NoteDetailsForm(
        val title: String,
        val content: String,
        val color: Long,
    )

    private val picturesMutableSharedFlow = MutableStateFlow<Set<PictureViewState>>(setOf())

    private val enabledTextStylesMutableStateFlow = MutableStateFlow(emptyList<ActionsSealed>())

    private val noteDetailsEventMutableSharedFlow = MutableSharedFlow<NoteDetailsEvent>(extraBufferCapacity = 1)
    val noteDetailsEventFlow: Flow<NoteDetailsEvent> = noteDetailsEventMutableSharedFlow.asSharedFlow()

    val contentFlow = MutableStateFlow("")

    val noteDetailsFlow: StateFlow<NoteDetailsViewState> = combine(
        noteDetailsViewStateMutableSharedFlow,
        picturesMutableSharedFlow,
        enabledTextStylesMutableStateFlow,
        contentFlow
    ) { note, pictures, enabledTextStyles, content ->
        NoteDetailsViewState(
            id = note.id,
            title = note.title,
            content = if (content == "") note.content else content,
            color = note.color,
            dateText = note.dateText,
            dateValue = note.dateValue,
            actions = ActionsSealed.values().map { availableTextStyle ->
                val isEnabled = enabledTextStyles.contains(availableTextStyle)

                EditTextAction(textStyle = availableTextStyle,
                    icon = availableTextStyle.icon,
                    background = availableTextStyle.background,
                    enabled = isEnabled,
                    onClickedCallback = EquatableCallback {
                        onItemClicked(availableTextStyle)
                    })
            })
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds), NoteDetailsViewState(
            id = 0,
            title = "",
            content = "",
            color = listOfColors().random(),
            dateText = NativeText.Argument(R.string.last_update, ZonedDateTime.now(fixedClock).format(dateTimeFormatter)),
            dateValue = ZonedDateTime.now(fixedClock).toInstant(),
            actions = listOf()
        )
    )

    private fun onItemClicked(actionSealed: ActionsSealed) {
        enabledTextStylesMutableStateFlow.update { list ->
            when (actionSealed.type) {
                ActionType.REVERSE -> {
                    noteDetailsEventMutableSharedFlow.tryEmit(NoteDetailsEvent.Revert(actionSealed.icon))
                    list
                }

                ActionType.TEXT_STYLE -> {
                    editorActionsSingleLiveEvent.setValue(actionSealed.icon)
                    if (list.contains(actionSealed)) {
                        actionSealed.background = R.drawable.custom_item_background
                        list - actionSealed
                    } else {
                        actionSealed.background = R.drawable.custom_item_background_pressed
                        list + actionSealed
                    }
                }

                ActionType.X2 -> {
                    list
                }

                ActionType.HEADING -> {
                    var currentList = emptyList<ActionsSealed>()
                    val headingItem = list.find { it.type == ActionType.HEADING }

                    if (headingItem != null) {
                        currentList = list - headingItem
                    }

                    headingItem?.background = R.drawable.custom_item_background

                    if (list.contains(actionSealed)) {
                        editorActionsSingleLiveEvent.setValue(actionSealed.icon)
                        actionSealed.background = R.drawable.custom_item_background
                        currentList - actionSealed
                    } else {
                        editorActionsSingleLiveEvent.setValue(actionSealed.icon)
                        actionSealed.background = R.drawable.custom_item_background_pressed
                        currentList + actionSealed
                    }
                }

                ActionType.COLOR -> {
                    list
                }

                ActionType.INSERT -> {
                    list
                }

                ActionType.ALIGN -> {
                    var currentList = emptyList<ActionsSealed>()
                    val headingItem = list.find { it.type == ActionType.ALIGN }

                    if (headingItem != null) {
                        currentList = list - headingItem
                    }

                    headingItem?.background = R.drawable.custom_item_background

                    if (list.contains(actionSealed)) {
                        editorActionsSingleLiveEvent.setValue(actionSealed.icon)
                        actionSealed.background = R.drawable.custom_item_background
                        currentList - actionSealed
                    } else {
                        editorActionsSingleLiveEvent.setValue(actionSealed.icon)
                        actionSealed.background = R.drawable.custom_item_background_pressed
                        currentList + actionSealed
                    }
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            val id = getCurrentIdFlowUseCase.invoke().firstOrNull()
            Log.i("GetNoteDetails", "id : $id")
            val noteWithPictures = id?.let { getNoteByIdFlowUseCase.invoke(it).firstOrNull() }
            if (noteWithPictures == null) {
                noteDetailsViewStateMutableSharedFlow.tryEmit(
                    NoteDetailsForm(
                        id = 0,
                        title = "",
                        content = "",
                        color = listOfColors().random(),
                        date = ZonedDateTime.now(fixedClock).format(dateTimeFormatter),
                        dateValue = ZonedDateTime.now(fixedClock).toInstant(),
                        actions = listOf()
                    )
                )
            } else {
//                    _noteTitle.value = noteWithPictures.noteEntity.title
//                    _noteContent.value = noteWithPictures.noteEntity.content
//                    _noteColor.value = noteWithPictures.noteEntity.color

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
                contentFlow.tryEmit(noteWithPictures.noteEntity.content)
            }
        }
    }

    fun onSaveNoteMenuItemClicked(title: String, content: String, updatedColor: Long) {
        viewModelScope.launch {
            getCurrentIdFlowUseCase.invoke().collect { id ->
                if (isEmptyOrBlank(title) || isEmptyOrBlank(content)) {
                        toastMessageSingleLiveEvent.setValue(NativeText.Resource(R.string.field_empty_toast_msg))
                } else {
                    val lastUpdateDate = ZonedDateTime.now(fixedClock).toInstant()

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

                        finishSavingSingleLiveEvent.setValue(Unit)
                }
            }
        }
    }

    fun onDeleteNoteMenuItemClicked() {
        viewModelScope.launch {
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

                    finishSavingSingleLiveEvent.setValue(Unit)
            }
        }
    }

    fun updateEditTextContent(content: String) {
        contentFlow.update {
            content
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

    fun onEvent(event: NoteDetailsEvent) {
        when (event) {
            is NoteDetailsEvent.ChangeTitle -> {

            }

            is NoteDetailsEvent.ChangeContent -> {

            }

            is NoteDetailsEvent.ChangeColor -> {

            }


            is NoteDetailsEvent.SaveNote -> {

            }

            else -> {

            }
        }
    }

    fun listOfColors() = listOf(0xFFffab91, 0xFFe8ed9d, 0xFFd095db, 0xFF7fdeea, 0xFFf48fb1)
}