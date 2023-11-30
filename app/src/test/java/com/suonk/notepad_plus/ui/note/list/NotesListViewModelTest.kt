package com.suonk.notepad_plus.ui.note.list

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.domain.use_cases.note.filter_sort.GetCurrentSortFilterUseCase
import com.suonk.notepad_plus.domain.use_cases.note.filter_sort.GetSortingParametersUseCase
import com.suonk.notepad_plus.domain.use_cases.note.filter_sort.SetCurrentSortFilterUseCase
import com.suonk.notepad_plus.domain.use_cases.note.get_note.GetAllNotesFlowUseCase
import com.suonk.notepad_plus.domain.use_cases.note.id.SetCurrentNoteIdUseCase
import com.suonk.notepad_plus.domain.use_cases.note.search.GetSearchNoteUseCase
import com.suonk.notepad_plus.domain.use_cases.note.search.SetSearchNoteUseCase
import com.suonk.notepad_plus.domain.use_cases.note.upsert.UpsertNoteUseCase
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import com.suonk.notepad_plus.model.database.data.entities.NoteEntityWithPictures
import com.suonk.notepad_plus.utils.EquatableCallback
import com.suonk.notepad_plus.utils.Sorting
import com.suonk.notepad_plus.utils.TestCoroutineRule
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

class NotesListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val getAllNotesFlowUseCase: GetAllNotesFlowUseCase = mockk()

    private val getSearchNoteUseCase: GetSearchNoteUseCase = mockk()
    private val setSearchNoteUseCase: SetSearchNoteUseCase = mockk()

    private val getSortingParametersUseCase: GetSortingParametersUseCase = mockk()

    private val getCurrentSortFilterNoteUseCase: GetCurrentSortFilterUseCase = mockk()
    private val setCurrentSortFilterNoteUseCase: SetCurrentSortFilterUseCase = mockk()

    private val upsertNoteUseCase: UpsertNoteUseCase = mockk()
    private val setCurrentNoteIdUseCase: SetCurrentNoteIdUseCase = mockk()

    private val application: Application = mockk()

    private lateinit var notesListViewModel: NotesListViewModel

    @Before
    fun setup() {
        every { application.getString(R.string.date_asc) } returns DEFAULT_DATE_ASC
        every { application.getString(R.string.date_desc) } returns DEFAULT_DATE_DESC
        every { application.getString(R.string.title_asc) } returns DEFAULT_TITLE_ASC
        every { application.getString(R.string.title_desc) } returns DEFAULT_TITLE_DESC
        every { application.getString(R.string.content_a_z) } returns DEFAULT_CONTENT_ASC
        every { application.getString(R.string.content_z_a) } returns DEFAULT_CONTENT_DESC
        every { application.getString(R.string.by_color) } returns DEFAULT_BY_COLOR

        every { application.getString(R.string.remove_filter) } returns REMOVE_FILTER
        every { application.getString(R.string.orange) } returns ORANGE
        every { application.getString(R.string.pink) } returns PINK
        every { application.getString(R.string.green) } returns GREEN
        every { application.getString(R.string.yellow) } returns YELLOW
        every { application.getString(R.string.purple) } returns PURPLE
        every { application.getString(R.string.blue) } returns BLUE

        every { getSearchNoteUseCase.invoke() } returns flowOf(DEFAULT_EMPTY_SEARCH)
        every { getSortingParametersUseCase.invoke() } returns flowOf(DATE_ASC)
        every { getCurrentSortFilterNoteUseCase.invoke() } returns flowOf(REMOVE_FILTER_INT)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        // GIVEN
        every { getAllNotesFlowUseCase.invoke() } returns flowOf(defaultAllNotesWithPicturesList())

        notesListViewModel = NotesListViewModel(
            getAllNotesFlowUseCase = getAllNotesFlowUseCase,

            getSearchNoteUseCase = getSearchNoteUseCase,
            setSearchNoteUseCase = setSearchNoteUseCase,

            getSortingParametersUseCase = getSortingParametersUseCase,

            getCurrentSortFilterNoteUseCase = getCurrentSortFilterNoteUseCase,
            setCurrentSortFilterNoteUseCase = setCurrentSortFilterNoteUseCase,

            upsertNoteUseCase = upsertNoteUseCase,
            setCurrentNoteIdUseCase = setCurrentNoteIdUseCase,
            application = application,
        )

        // WHEN
//        notesListViewModel.notesListFlow.test {
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
    }

    @Test
    fun `initial case`() = testCoroutineRule.runTest {
        every { getAllNotesFlowUseCase.invoke() } returns flowOf(emptyList())

        notesListViewModel = NotesListViewModel(
            getAllNotesFlowUseCase = getAllNotesFlowUseCase,

            getSearchNoteUseCase = getSearchNoteUseCase,
            setSearchNoteUseCase = setSearchNoteUseCase,

            getSortingParametersUseCase = getSortingParametersUseCase,

            getCurrentSortFilterNoteUseCase = getCurrentSortFilterNoteUseCase,
            setCurrentSortFilterNoteUseCase = setCurrentSortFilterNoteUseCase,


            upsertNoteUseCase = upsertNoteUseCase,
            setCurrentNoteIdUseCase = setCurrentNoteIdUseCase,
            application = application,
        )

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

        private const val DEFAULT_DATE_ASC = "Du + récent au + ancien"
        private val DATE_ASC = Sorting.DATE_ASC

        private const val DEFAULT_DATE_DESC = "Du + ancien au + récent"
        private const val DEFAULT_TITLE_ASC = "Titre A-Z"
        private const val DEFAULT_TITLE_DESC = "Titre Z-A"
        private const val DEFAULT_CONTENT_ASC = "Content A-Z"
        private const val DEFAULT_CONTENT_DESC = "Content Z-A"
        private const val DEFAULT_BY_COLOR = "By color"

        private const val REMOVE_FILTER = "Remove filter"
        private val REMOVE_FILTER_INT = R.string.remove_filter

        private const val ORANGE = "Orange"
        private const val PINK = "Pink"
        private const val GREEN = "Green"
        private const val YELLOW = "Yellow"
        private const val PURPLE = "Purple"
        private const val BLUE = "Blue"
    }
}