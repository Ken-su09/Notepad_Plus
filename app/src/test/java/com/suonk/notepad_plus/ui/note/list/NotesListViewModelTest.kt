package com.suonk.notepad_plus.ui.note.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.suonk.notepad_plus.domain.use_cases.note.get_note.GetAllNotesFlowUseCase
import com.suonk.notepad_plus.domain.use_cases.note.id.SetCurrentNoteIdUseCase
import com.suonk.notepad_plus.domain.use_cases.note.search.GetSearchNoteUseCase
import com.suonk.notepad_plus.domain.use_cases.note.search.SetSearchNoteUseCase
import com.suonk.notepad_plus.domain.use_cases.note.upsert.UpsertNoteUseCase
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import com.suonk.notepad_plus.model.database.data.entities.NoteEntityWithPictures
import com.suonk.notepad_plus.utils.EquatableCallback
import com.suonk.notepad_plus.utils.TestCoroutineRule
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


class NotesListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val getAllNotesFlowUseCase: GetAllNotesFlowUseCase = mockk()
    private val getSearchNoteUseCase: GetSearchNoteUseCase = mockk()
    private val setSearchNoteUseCase: SetSearchNoteUseCase = mockk()
    private val upsertNoteUseCase: UpsertNoteUseCase = mockk()
    private val setCurrentNoteIdUseCase: SetCurrentNoteIdUseCase = mockk()

    private lateinit var notesListViewModel: NotesListViewModel

    @Before
    fun setup() {
    }

//    @Test
//    fun `nominal case`() = testCoroutineRule.runTest {
//        // GIVEN
//        every { getAllNotesFlowUseCase.invoke() } returns flowOf(defaultAllNotesWithPicturesList())
//        every { getSearchNoteUseCase.invoke() } returns flowOf(DEFAULT_EMPTY_SEARCH)
//
//        notesListViewModel = NotesListViewModel(
//            getAllNotesFlowUseCase = getAllNotesFlowUseCase,
//            getSearchNoteUseCase = getSearchNoteUseCase,
//            setSearchNoteUseCase = setSearchNoteUseCase,
//            upsertNoteUseCase = upsertNoteUseCase,
//            setCurrentNoteIdUseCase = setCurrentNoteIdUseCase,
//        )
//
//        // WHEN
//        notesListViewModel.notesListFlow.test {
//            runCurrent()
//
//            // THEN
//            assertEquals(defaultAllNotesViewState(), awaitItem())
//
//            verify(exactly = 1) {
//                getAllNotesFlowUseCase.invoke()
//                getSearchNoteUseCase.invoke()
//            }
//
//            confirmVerified(getAllNotesFlowUseCase, getSearchNoteUseCase)
//        }
//    }

    @Test
    fun `initial case`() = testCoroutineRule.runTest {
        every { getAllNotesFlowUseCase.invoke() } returns flowOf(emptyList())
        every { getSearchNoteUseCase.invoke() } returns flowOf(DEFAULT_EMPTY_SEARCH)

//        notesListViewModel = NotesListViewModel(
//            getAllNotesFlowUseCase = getAllNotesFlowUseCase,
//            getSearchNoteUseCase = getSearchNoteUseCase,
//            setSearchNoteUseCase = setSearchNoteUseCase,
//            upsertNoteUseCase = upsertNoteUseCase,
//            setCurrentNoteIdUseCase = setCurrentNoteIdUseCase,
//        )

        // WHEN
        notesListViewModel.notesListFlow.test {
            // THEN
            assertTrue(awaitItem().isEmpty())

            verify {
                getAllNotesFlowUseCase.invoke()
                getSearchNoteUseCase.invoke()
            }

            confirmVerified(getAllNotesFlowUseCase, getSearchNoteUseCase)
        }
    }

    private fun defaultAllNotesWithPicturesList() = listOf(
        NoteEntityWithPictures(
            NoteEntity(
                id = NOTE_ID_1,
                title = NOTE_TITLE_1,
                content = NOTE_CONTENT_1,
                date = NOTE_DATE_1,
                color = NOTE_COLOR_1,
                isFavorite = false,
                isDeleted = false
            ), listOf()
        ), NoteEntityWithPictures(
            NoteEntity(
                id = NOTE_ID_2,
                title = NOTE_TITLE_2,
                content = NOTE_CONTENT_2,
                date = NOTE_DATE_2,
                color = NOTE_COLOR_2,
                isFavorite = false,
                isDeleted = false
            ), listOf()
        )
    )

    private fun defaultAllNotesViewState() = listOf(
        NotesListViewState(
            id = NOTE_ID_1,
            title = NOTE_TITLE_1,
            content = NOTE_CONTENT_1,
            date = NOTE_FORMAT_DATE_1,
            color = NOTE_COLOR_1,
            onItemNoteClicked = EquatableCallback { },
            onDeleteNoteClicked = EquatableCallback { },
            actions = emptyList()
        ), NotesListViewState(
            id = NOTE_ID_2,
            title = NOTE_TITLE_2,
            content = NOTE_CONTENT_2,
            date = NOTE_FORMAT_DATE_2,
            color = NOTE_COLOR_2,
            onItemNoteClicked = EquatableCallback { },
            onDeleteNoteClicked = EquatableCallback { },
            actions = emptyList()
        )
    )

    companion object {
        private const val NOTE_ID_1 = 1L
        private const val NOTE_ID_2 = 2L

        private const val NOTE_TITLE_1 = "First News of the Week"
        private const val NOTE_TITLE_2 = "Je pense que Studio Jams est meilleur"

        private const val NOTE_CONTENT_1 = "Règle : Nombre aléatoire en 10 - 255"
        private const val NOTE_CONTENT_2 = "Le niveau qu'il faut pour se coordonner à plusieurs instruments avec des gens que t'as jamais vu"

        private const val NOTE_COLOR_1 = 0xFFffab91
        private const val NOTE_COLOR_2 = 0xFFe8ed9d

        private const val DEFAULT_TIMESTAMP_LONG = 1_000_000_000L // 09/09/2001 - 01:46:40
        private val fixedClock = Clock.fixed(Instant.ofEpochSecond(DEFAULT_TIMESTAMP_LONG), ZoneOffset.UTC)
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

        private val NOTE_DATE_1 = LocalDateTime.now(fixedClock)
        private val NOTE_FORMAT_DATE_1 = NOTE_DATE_1.format(dateTimeFormatter)
        private val NOTE_DATE_2 = LocalDateTime.now(fixedClock)
        private val NOTE_FORMAT_DATE_2 = NOTE_DATE_2.format(dateTimeFormatter)

        private const val DEFAULT_EMPTY_SEARCH = ""
    }
}