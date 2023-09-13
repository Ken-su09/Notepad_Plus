package com.suonk.notepad_plus.ui.note.details

import android.app.Application
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.domain.use_cases.note.get.GetNoteByIdFlowUseCase
import com.suonk.notepad_plus.domain.use_cases.note.id.GetCurrentIdFlowUseCase
import com.suonk.notepad_plus.domain.use_cases.note.upsert.UpsertNoteUseCase
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import com.suonk.notepad_plus.ui.note.details.actions_list.EditTextAction
import com.suonk.notepad_plus.utils.CoroutineDispatcherProvider
import com.suonk.notepad_plus.utils.EquatableCallback
import com.suonk.notepad_plus.utils.NativeText
import com.suonk.notepad_plus.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
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

@HiltViewModel
class NoteDetailsViewModel @Inject constructor(
    private val getNoteByIdFlowUseCase: GetNoteByIdFlowUseCase,
    private val getCurrentIdFlowUseCase: GetCurrentIdFlowUseCase,
    private val upsertNoteUseCase: UpsertNoteUseCase,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val fixedClock: Clock,
    private val application: Application
) : ViewModel() {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    private var noteDetailsViewStateMutableSharedFlow = MutableSharedFlow<NoteDetailsViewState>(replay = 1)
    private var picturesMutableSharedFlow = MutableStateFlow<Set<PictureViewState>>(setOf())

    private val editTextActionsMutableStateFlow = MutableStateFlow(
        listOf(
            EditTextAction(R.drawable.ic_undo, EquatableCallback {
                editorActionsSingleLiveEvent.setValue(R.drawable.ic_undo)
            }, AppCompatResources.getDrawable(application, R.drawable.custom_item_background)),
            EditTextAction(R.drawable.ic_redo, EquatableCallback {
                editorActionsSingleLiveEvent.setValue(R.drawable.ic_redo)
            }, AppCompatResources.getDrawable(application, R.drawable.custom_item_background)),
            getEditTextActionWithBackgroundChanges(R.drawable.ic_bold),
            getEditTextActionWithBackgroundChanges(R.drawable.ic_italic),
            getEditTextActionWithBackgroundChanges(R.drawable.ic_x2_subscript),
            getEditTextActionWithBackgroundChanges(R.drawable.ic_x2_squared),
            getEditTextActionWithBackgroundChanges(R.drawable.ic_heading_1),
            getEditTextActionWithBackgroundChanges(R.drawable.ic_heading_2),
            getEditTextActionWithBackgroundChanges(R.drawable.ic_heading_3),
            getEditTextActionWithBackgroundChanges(R.drawable.ic_heading_4),
            getEditTextActionWithBackgroundChanges(R.drawable.ic_heading_5),
            getEditTextActionWithBackgroundChanges(R.drawable.ic_heading_6),
            getEditTextActionWithBackgroundChanges(R.drawable.ic_text_color),
            getEditTextActionWithBackgroundChanges(R.drawable.ic_background_color),
            getEditTextActionWithBackgroundChanges(R.drawable.ic_insert_image),
            getEditTextActionWithBackgroundChanges(R.drawable.ic_left_align),
            getEditTextActionWithBackgroundChanges(R.drawable.ic_center_align),
            getEditTextActionWithBackgroundChanges(R.drawable.ic_right_align),
            getEditTextActionWithBackgroundChanges(R.drawable.ic_un_ordered_list),
            getEditTextActionWithBackgroundChanges(R.drawable.ic_numbered_list),
        )
    )

    val finishSavingSingleLiveEvent = SingleLiveEvent<Unit>()
    val editorActionsSingleLiveEvent = SingleLiveEvent<Int>()
    var toastMessageSingleLiveEvent = SingleLiveEvent<NativeText>()


    val noteDetailsViewState: LiveData<NoteDetailsViewState> = liveData(coroutineDispatcherProvider.io) {
        combine(
            noteDetailsViewStateMutableSharedFlow,
            picturesMutableSharedFlow,
            editTextActionsMutableStateFlow
        ) { note, pictures, actions ->
            Log.i("GetEditTextAction", "actions : $actions")

            emit(
                NoteDetailsViewState(
                    id = note.id,
                    title = note.title,
                    content = note.content,
                    dateText = note.dateText,
                    dateValue = note.dateValue,
                    actions = actions
                )
            )
        }.collect()
    }

    init {
        viewModelScope.launch(coroutineDispatcherProvider.io) {
            getCurrentIdFlowUseCase.invoke().collect { id ->
                Log.i("GET_NOTE", "id : $id")

                val noteWithPictures = id?.let { getNoteByIdFlowUseCase.invoke(it).firstOrNull() }
                if (noteWithPictures == null) {
                    noteDetailsViewStateMutableSharedFlow.tryEmit(
                        NoteDetailsViewState(
                            id = 0,
                            title = "",
                            content = "",
                            dateText = NativeText.Argument(R.string.last_update, ZonedDateTime.now(fixedClock).format(dateTimeFormatter)),
                            dateValue = ZonedDateTime.now(fixedClock).toInstant(),
                            actions = listOf()
                        )
                    )
                } else {
                    noteDetailsViewStateMutableSharedFlow.tryEmit(
                        NoteDetailsViewState(
                            id = noteWithPictures.noteEntity.id,
                            title = noteWithPictures.noteEntity.title,
                            content = noteWithPictures.noteEntity.content,
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

    fun onSaveNoteMenuItemClicked(title: String, content: String) {
        viewModelScope.launch(coroutineDispatcherProvider.io) {
            getCurrentIdFlowUseCase.invoke().collect { id ->
                if (isEmptyOrBlank(title) || isEmptyOrBlank(content)) {
                    withContext(coroutineDispatcherProvider.main) {
                        toastMessageSingleLiveEvent.setValue(NativeText.Resource(R.string.field_empty_toast_msg))
                    }
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

                    withContext(coroutineDispatcherProvider.main) {
                        finishSavingSingleLiveEvent.setValue(Unit)
                    }
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
                    finishSavingSingleLiveEvent.setValue(Unit)
                }
            }
        }
    }

    private fun fromLocalDateToInstant(value: LocalDateTime): Instant = value.toInstant(ZoneOffset.UTC)
    private fun fromInstantToLocalDate(instant: Instant): LocalDateTime = instant.atZone(ZoneOffset.UTC).toLocalDateTime()

    private fun isEmptyOrBlank(value: String): Boolean {
        return value.isEmpty() || value.isBlank() || value == "" || value == " "
    }

    private fun updateItem(id: Int) {
        editTextActionsMutableStateFlow.update { list ->
            Log.i("GetEditTextAction", "updateItem - list : $list")
            val action = list.find { it.icon == id }
            if (action?.background == AppCompatResources.getDrawable(application, R.drawable.custom_item_background_pressed)) {
                Log.i("GetEditTextAction", "updateItem - list : true")
                action?.background = AppCompatResources.getDrawable(application, R.drawable.custom_item_background)
            } else {
                Log.i("GetEditTextAction", "updateItem - list : false")
                action?.background = AppCompatResources.getDrawable(application, R.drawable.custom_item_background_pressed)
            }
            list
        }
    }

    private fun getEditTextActionWithBackgroundChanges(icon: Int): EditTextAction {
        return EditTextAction(icon, EquatableCallback {
            updateItem(icon)
            editorActionsSingleLiveEvent.setValue(icon)
        }, AppCompatResources.getDrawable(application, R.drawable.custom_item_background))
    }
}