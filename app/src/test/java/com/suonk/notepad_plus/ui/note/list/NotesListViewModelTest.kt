package com.suonk.notepad_plus.ui.note.list

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.domain.filter.GetFilterParametersUseCase
import com.suonk.notepad_plus.domain.sort.GetSortingParametersUseCase
import com.suonk.notepad_plus.domain.filter.SetFilterParametersUseCase
import com.suonk.notepad_plus.domain.note.get_note.GetAllNotesFlowUseCase
import com.suonk.notepad_plus.domain.note.id.SetCurrentNoteIdUseCase
import com.suonk.notepad_plus.domain.search.GetSearchNoteUseCase
import com.suonk.notepad_plus.domain.search.SetSearchNoteUseCase
import com.suonk.notepad_plus.domain.note.upsert.UpsertNoteUseCase
import com.suonk.notepad_plus.domain.sort.SetSortingParametersUseCase
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import com.suonk.notepad_plus.model.database.data.entities.NoteEntityWithPictures
import com.suonk.notepad_plus.utils.EquatableCallback
import com.suonk.notepad_plus.utils.Filtering
import com.suonk.notepad_plus.utils.Sorting
import com.suonk.notepad_plus.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val getSortingParametersUseCase: GetSortingParametersUseCase = mockk()
    private val setSortingParametersUseCase: SetSortingParametersUseCase = mockk()

    private val getFilterParametersNoteUseCase: GetFilterParametersUseCase = mockk()
    private val setFilterParametersUseCase: SetFilterParametersUseCase = mockk()

    private val upsertNoteUseCase: UpsertNoteUseCase = mockk()
    private val setCurrentNoteIdUseCase: SetCurrentNoteIdUseCase = mockk()

    private val application: Application = mockk()

    private lateinit var notesListViewModel: NotesListViewModel

    private fun setupViewModel() {
        notesListViewModel = NotesListViewModel(
            getAllNotesFlowUseCase = getAllNotesFlowUseCase,

            getSearchNoteUseCase = getSearchNoteUseCase,
            setSearchNoteUseCase = setSearchNoteUseCase,

            getSortingParametersUseCase = getSortingParametersUseCase,
            setSortingParametersUseCase = setSortingParametersUseCase,

            getFilterParametersNoteUseCase = getFilterParametersNoteUseCase,
            setFilterParametersUseCase = setFilterParametersUseCase,

            upsertNoteUseCase = upsertNoteUseCase,
            setCurrentNoteIdUseCase = setCurrentNoteIdUseCase,
        )
    }

    @Before
    fun setup() {
        val currentSearchParameterFlow = MutableStateFlow<String?>(null)
        every { getSearchNoteUseCase.invoke() } returns currentSearchParameterFlow
        every { getSortingParametersUseCase.invoke() } returns flowOf(DATE_ASC)
        every { getFilterParametersNoteUseCase.invoke() } returns flowOf(REMOVE_FILTER)
        every { getAllNotesFlowUseCase.invoke() } returns flowOf(defaultAllNotesWithPicturesList())
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        // GIVEN
        setupViewModel()

        // WHEN
        notesListViewModel.notesListFlow.test {
            // THEN
            awaitItem()

            val expectedListOfNotes = awaitItem()
            assertEquals(defaultAllNotesViewState(), expectedListOfNotes)

            verify(exactly = 1) {
                getAllNotesFlowUseCase.invoke()
                getSearchNoteUseCase.invoke()
            }

            confirmVerified(getAllNotesFlowUseCase, getSearchNoteUseCase)
        }
    }

    @Test
    fun `initial case`() = testCoroutineRule.runTest {
        every { getAllNotesFlowUseCase.invoke() } returns flowOf(emptyList())

        setupViewModel()

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

    @Test
    fun `nominal case then filter in pink`() = testCoroutineRule.runTest {
        // GIVEN
        coJustRun { setFilterParametersUseCase.invoke(PINK_FILTER) }
        val currentSortFilterFlow = MutableStateFlow(REMOVE_FILTER)
        every { getFilterParametersNoteUseCase.invoke() } returns currentSortFilterFlow

        setupViewModel()

        notesListViewModel.notesListFlow.test {
            awaitItem()
            assertEquals(defaultAllNotesViewState(), awaitItem())

            // WHEN
            currentSortFilterFlow.tryEmit(PINK_FILTER)

            // THEN
            assertTrue(awaitItem().isEmpty())
            println(5)

            coVerify(exactly = 1) {
                getAllNotesFlowUseCase.invoke()
                getSearchNoteUseCase.invoke()
                getFilterParametersNoteUseCase.invoke()
            }

            confirmVerified(getAllNotesFlowUseCase, getSearchNoteUseCase, getFilterParametersNoteUseCase)
        }
    }

    //region ===================================================================== SEARCH =====================================================================

    @Test
    fun `set search parameters`() = testCoroutineRule.runTest {
        // GIVEN
        justRun { setSearchNoteUseCase.invoke(DEFAULT_SEARCH) }

        setupViewModel()

        notesListViewModel.setSearchParameters(DEFAULT_SEARCH)

        coVerify(exactly = 1) {
            getAllNotesFlowUseCase.invoke()
            getSearchNoteUseCase.invoke()
            setSearchNoteUseCase.invoke(DEFAULT_SEARCH)
        }

        confirmVerified(getAllNotesFlowUseCase, getSearchNoteUseCase, setSearchNoteUseCase)
    }

    @Test
    fun `nominal case with search with value that is in both title and content`() = testCoroutineRule.runTest {
        // GIVEN
        every { getSearchNoteUseCase.invoke() } returns flowOf(BOTH_SEARCH)

        setupViewModel()

        // WHEN
        notesListViewModel.notesListFlow.test {
            // THEN
            awaitItem()

            val expectedListOfNotes = awaitItem()
            assertEquals(defaultAllNotesViewStateAfterSearchForBothTitleContent(), expectedListOfNotes)

            verify(exactly = 1) {
                getAllNotesFlowUseCase.invoke()
                getSearchNoteUseCase.invoke()
            }

            confirmVerified(getAllNotesFlowUseCase, getSearchNoteUseCase)
        }
    }

    @Test
    fun `nominal case with search with value that is in the title`() = testCoroutineRule.runTest {
        // GIVEN
        every { getSearchNoteUseCase.invoke() } returns flowOf(FIRST_NEWS_SEARCH)

        setupViewModel()

        // WHEN
        notesListViewModel.notesListFlow.test {
            // THEN
            awaitItem()

            val expectedListOfNotes = awaitItem()
            assertEquals(defaultAllNotesViewStateAfterSearchForTitleOrContent(), expectedListOfNotes)

            verify(exactly = 1) {
                getAllNotesFlowUseCase.invoke()
                getSearchNoteUseCase.invoke()
            }

            confirmVerified(getAllNotesFlowUseCase, getSearchNoteUseCase)
        }
    }

    @Test
    fun `nominal case with search with value that is in the content`() = testCoroutineRule.runTest {
        // GIVEN
        every { getSearchNoteUseCase.invoke() } returns flowOf(RANDOM_NUMBER_SEARCH)

        setupViewModel()

        // WHEN
        notesListViewModel.notesListFlow.test {
            // THEN
            awaitItem()

            val expectedListOfNotes = awaitItem()
            assertEquals(defaultAllNotesViewStateAfterSearchForTitleOrContent(), expectedListOfNotes)

            verify(exactly = 1) {
                getAllNotesFlowUseCase.invoke()
                getSearchNoteUseCase.invoke()
            }

            confirmVerified(getAllNotesFlowUseCase, getSearchNoteUseCase)
        }
    }

    @Test
    fun `set search parameters with null`() = testCoroutineRule.runTest {
        // GIVEN
        justRun { setSearchNoteUseCase.invoke(null) }

        setupViewModel()

        notesListViewModel.setSearchParameters(null)

        coVerify(exactly = 1) {
            getAllNotesFlowUseCase.invoke()
            getSearchNoteUseCase.invoke()
            setSearchNoteUseCase.invoke(null)
        }

        confirmVerified(getAllNotesFlowUseCase, getSearchNoteUseCase, setSearchNoteUseCase)
    }

    //endregion

    //region =============================================================== EQUATABLE CALLBACK ===============================================================

    @Test
    fun `nominal case leads to on item note clicked`() = testCoroutineRule.runTest {
        // GIVEN
        justRun { setCurrentNoteIdUseCase.invoke(NOTE_ID_1) }

        setupViewModel()

        // WHEN
        notesListViewModel.notesListFlow.test {
            // THEN
            awaitItem()

            val expectedListOfNotes = awaitItem()
            expectedListOfNotes[0].onItemNoteClicked()

            coVerify(exactly = 1) {
                getAllNotesFlowUseCase.invoke()
                getSearchNoteUseCase.invoke()
                setCurrentNoteIdUseCase.invoke(NOTE_ID_1)
            }

            confirmVerified(getAllNotesFlowUseCase, getSearchNoteUseCase, setCurrentNoteIdUseCase)
        }
    }

    @Test
    fun `nominal case leads to delete clicked`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { upsertNoteUseCase.invoke(firstNoteEntityDeleted()) } returns NOTE_ID_1

        setupViewModel()

        // WHEN
        notesListViewModel.notesListFlow.test {
            // THEN
            awaitItem()

            val expectedListOfNotes = awaitItem()
            expectedListOfNotes[0].onDeleteNoteClicked()

            coVerify(exactly = 1) {
                getAllNotesFlowUseCase.invoke()
                getSearchNoteUseCase.invoke()
//                upsertNoteUseCase.invoke(firstNoteEntityDeleted())
            }

            confirmVerified(getAllNotesFlowUseCase, getSearchNoteUseCase, upsertNoteUseCase)
        }
    }

    //endregion

    //region ================================================================= DEFAULT VALUES =================================================================

    private fun defaultAllNotesWithPicturesList() = listOf(
        NoteEntityWithPictures(
            firstNoteEntity(), listOf()
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

    private fun firstNoteEntity() = NoteEntity(
        id = NOTE_ID_1,
        title = NOTE_TITLE_1,
        content = NOTE_CONTENT_1,
        date = NOTE_DATE_1,
        color = NOTE_COLOR_1,
        isFavorite = false,
        isDeleted = false
    )

    private fun firstNoteEntityDeleted() = NoteEntity(
        id = NOTE_ID_1, title = NOTE_TITLE_1, content = NOTE_CONTENT_1, date = NOTE_DATE_1, color = NOTE_COLOR_1, isFavorite = false, isDeleted = true
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

    private fun defaultAllNotesViewStateAfterSearchForTitleOrContent() = listOf(
        NotesListViewState(
            id = NOTE_ID_1,
            title = NOTE_TITLE_1,
            content = NOTE_CONTENT_1,
            date = NOTE_FORMAT_DATE_1,
            color = NOTE_COLOR_1,
            onItemNoteClicked = EquatableCallback { },
            onDeleteNoteClicked = EquatableCallback { },
            actions = emptyList()
        )
    )

    private fun defaultAllNotesViewStateAfterSearchForBothTitleContent() = listOf(
        NotesListViewState(
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

        private const val FIRST_NEWS_SEARCH = "First News"
        private const val RANDOM_NUMBER_SEARCH = "Nombre aléatoire"
        private const val BOTH_SEARCH = "que"
        private const val DEFAULT_SEARCH = "DEFAULT_SEARCH"

        private val DATE_ASC = Sorting.DATE_ASC
        private val REMOVE_FILTER = Filtering.REMOVE_FILTER
        private val REMOVE_FILTER_INT = R.string.remove_filter
        private val PINK_FILTER = Filtering.PINK
    }

    //endregion
}