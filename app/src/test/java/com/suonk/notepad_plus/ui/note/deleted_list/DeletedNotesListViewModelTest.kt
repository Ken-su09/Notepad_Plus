package com.suonk.notepad_plus.ui.note.deleted_list

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.domain.use_cases.note.filter_sort.GetSortingParametersUseCase
import com.suonk.notepad_plus.domain.use_cases.note.get_note.GetAllDeletedNotesFlowUseCase
import com.suonk.notepad_plus.domain.use_cases.note.id.SetCurrentNoteIdUseCase
import com.suonk.notepad_plus.domain.use_cases.note.search.GetSearchNoteUseCase
import com.suonk.notepad_plus.domain.use_cases.note.search.SetSearchNoteUseCase
import com.suonk.notepad_plus.domain.use_cases.note.upsert.UpsertNoteUseCase
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import com.suonk.notepad_plus.model.database.data.entities.NoteEntityWithPictures
import com.suonk.notepad_plus.ui.note.list.NotesListViewModel
import com.suonk.notepad_plus.utils.EquatableCallback
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
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
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

class DeletedNotesListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val getAllDeletedNotesFlowUseCase: GetAllDeletedNotesFlowUseCase = mockk()

    private val getSearchNoteUseCase: GetSearchNoteUseCase = mockk()
    private val setSearchNoteUseCase: SetSearchNoteUseCase = mockk()

    private val getSortingParametersUseCase: GetSortingParametersUseCase = mockk()


    private val upsertNoteUseCase: UpsertNoteUseCase = mockk()
    private val setCurrentNoteIdUseCase: SetCurrentNoteIdUseCase = mockk()

    private lateinit var deletedNotesListViewModel: DeletedNotesListViewModel

    @Before
    fun setup() {
        val currentSearchParameterFlow = MutableStateFlow<String?>(null)
        every { getSearchNoteUseCase.invoke() } returns currentSearchParameterFlow
        every { getSortingParametersUseCase.invoke() } returns flowOf(DATE_ASC)
        every { getAllDeletedNotesFlowUseCase.invoke() } returns flowOf(defaultAllDeletedNotesWithPicturesList())
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        // GIVEN
        deletedNotesListViewModel = DeletedNotesListViewModel(
            getAllDeletedNotesFlowUseCase = getAllDeletedNotesFlowUseCase,

            getSearchNoteUseCase = getSearchNoteUseCase,
            setSearchNoteUseCase = setSearchNoteUseCase,

            upsertNoteUseCase = upsertNoteUseCase,
            setCurrentNoteIdUseCase = setCurrentNoteIdUseCase,
        )

        // WHEN
        deletedNotesListViewModel.deletedNotesListFlow.test {
            // THEN
            awaitItem()

            val expectedListOfNotes = awaitItem()
            assertEquals(defaultAllDeletedNotesViewState(), expectedListOfNotes)

            verify(exactly = 1) {
                getAllDeletedNotesFlowUseCase.invoke()
                getSearchNoteUseCase.invoke()
            }

            confirmVerified(getAllDeletedNotesFlowUseCase, getSearchNoteUseCase)
        }
    }

    @Test
    fun `initial case`() = testCoroutineRule.runTest {
        every { getAllDeletedNotesFlowUseCase.invoke() } returns flowOf(emptyList())

        deletedNotesListViewModel = DeletedNotesListViewModel(
            getAllDeletedNotesFlowUseCase = getAllDeletedNotesFlowUseCase,

            getSearchNoteUseCase = getSearchNoteUseCase,
            setSearchNoteUseCase = setSearchNoteUseCase,

            upsertNoteUseCase = upsertNoteUseCase,
            setCurrentNoteIdUseCase = setCurrentNoteIdUseCase,
        )

        // WHEN
        deletedNotesListViewModel.deletedNotesListFlow.test {
            // THEN
            TestCase.assertTrue(awaitItem().isEmpty())

            verify {
                getAllDeletedNotesFlowUseCase.invoke()
                getSearchNoteUseCase.invoke()
            }

            confirmVerified(getAllDeletedNotesFlowUseCase, getSearchNoteUseCase)
        }
    }

    //region ===================================================================== SEARCH =====================================================================

    @Test
    fun `set search parameters`() = testCoroutineRule.runTest {
        // GIVEN
        justRun { setSearchNoteUseCase.invoke(DEFAULT_SEARCH) }

        deletedNotesListViewModel = DeletedNotesListViewModel(
            getAllDeletedNotesFlowUseCase = getAllDeletedNotesFlowUseCase,

            getSearchNoteUseCase = getSearchNoteUseCase,
            setSearchNoteUseCase = setSearchNoteUseCase,

            upsertNoteUseCase = upsertNoteUseCase,
            setCurrentNoteIdUseCase = setCurrentNoteIdUseCase,
        )

        deletedNotesListViewModel.setSearchParameters(DEFAULT_SEARCH)

        coVerify(exactly = 1) {
            getAllDeletedNotesFlowUseCase.invoke()
            getSearchNoteUseCase.invoke()
            setSearchNoteUseCase.invoke(DEFAULT_SEARCH)
        }

        confirmVerified(getAllDeletedNotesFlowUseCase, getSearchNoteUseCase, setSearchNoteUseCase)
    }

    @Test
    fun `nominal case with search with value that is in both title and content`() = testCoroutineRule.runTest {
        // GIVEN
        every { getSearchNoteUseCase.invoke() } returns flowOf(BOTH_SEARCH)

        deletedNotesListViewModel = DeletedNotesListViewModel(
            getAllDeletedNotesFlowUseCase = getAllDeletedNotesFlowUseCase,

            getSearchNoteUseCase = getSearchNoteUseCase,
            setSearchNoteUseCase = setSearchNoteUseCase,

            upsertNoteUseCase = upsertNoteUseCase,
            setCurrentNoteIdUseCase = setCurrentNoteIdUseCase,
        )

        // WHEN
        deletedNotesListViewModel.deletedNotesListFlow.test {
            // THEN
            awaitItem()

            val expectedListOfNotes = awaitItem()
            assertEquals(defaultAllDeletedNotesViewStateAfterSearchForBothTitleAndContent(), expectedListOfNotes)

            verify(exactly = 1) {
                getAllDeletedNotesFlowUseCase.invoke()
                getSearchNoteUseCase.invoke()
            }

            confirmVerified(getAllDeletedNotesFlowUseCase, getSearchNoteUseCase)
        }
    }

    @Test
    fun `nominal case with search with value that is in the title`() = testCoroutineRule.runTest {
        // GIVEN
        every { getSearchNoteUseCase.invoke() } returns flowOf(FIRST_NEWS_SEARCH)

        deletedNotesListViewModel = DeletedNotesListViewModel(
            getAllDeletedNotesFlowUseCase = getAllDeletedNotesFlowUseCase,

            getSearchNoteUseCase = getSearchNoteUseCase,
            setSearchNoteUseCase = setSearchNoteUseCase,

            upsertNoteUseCase = upsertNoteUseCase,
            setCurrentNoteIdUseCase = setCurrentNoteIdUseCase,
        )

        // WHEN
        deletedNotesListViewModel.deletedNotesListFlow.test {
            // THEN
            awaitItem()

            val expectedListOfNotes = awaitItem()
            assertEquals(defaultAllDeletedNotesViewStateAfterSearchForTitleOrContent(), expectedListOfNotes)

            verify(exactly = 1) {
                getAllDeletedNotesFlowUseCase.invoke()
                getSearchNoteUseCase.invoke()
            }

            confirmVerified(getAllDeletedNotesFlowUseCase, getSearchNoteUseCase)
        }
    }

    @Test
    fun `nominal case with search with value that is in the content`() = testCoroutineRule.runTest {
        // GIVEN
        every { getSearchNoteUseCase.invoke() } returns flowOf(RANDOM_NUMBER_SEARCH)

        deletedNotesListViewModel = DeletedNotesListViewModel(
            getAllDeletedNotesFlowUseCase = getAllDeletedNotesFlowUseCase,

            getSearchNoteUseCase = getSearchNoteUseCase,
            setSearchNoteUseCase = setSearchNoteUseCase,

            upsertNoteUseCase = upsertNoteUseCase,
            setCurrentNoteIdUseCase = setCurrentNoteIdUseCase,
        )

        // WHEN
        deletedNotesListViewModel.deletedNotesListFlow.test {
            // THEN
            awaitItem()

            val expectedListOfNotes = awaitItem()
            assertEquals(defaultAllDeletedNotesViewStateAfterSearchForTitleOrContent(), expectedListOfNotes)

            verify(exactly = 1) {
                getAllDeletedNotesFlowUseCase.invoke()
                getSearchNoteUseCase.invoke()
            }

            confirmVerified(getAllDeletedNotesFlowUseCase, getSearchNoteUseCase)
        }
    }

    @Test
    fun `set search parameters with null`() = testCoroutineRule.runTest {
        // GIVEN
        justRun { setSearchNoteUseCase.invoke(null) }

        deletedNotesListViewModel = DeletedNotesListViewModel(
            getAllDeletedNotesFlowUseCase = getAllDeletedNotesFlowUseCase,

            getSearchNoteUseCase = getSearchNoteUseCase,
            setSearchNoteUseCase = setSearchNoteUseCase,

            upsertNoteUseCase = upsertNoteUseCase,
            setCurrentNoteIdUseCase = setCurrentNoteIdUseCase,
        )

        deletedNotesListViewModel.setSearchParameters(null)

        coVerify(exactly = 1) {
            getAllDeletedNotesFlowUseCase.invoke()
            getSearchNoteUseCase.invoke()
            setSearchNoteUseCase.invoke(null)
        }

        confirmVerified(getAllDeletedNotesFlowUseCase, getSearchNoteUseCase, setSearchNoteUseCase)
    }

    //endregion

    //region =============================================================== EQUATABLE CALLBACK ===============================================================

    @Test
    fun `nominal case leads to on item note clicked`() = testCoroutineRule.runTest {
        // GIVEN
        justRun { setCurrentNoteIdUseCase.invoke(NOTE_ID_1) }

        deletedNotesListViewModel = DeletedNotesListViewModel(
            getAllDeletedNotesFlowUseCase = getAllDeletedNotesFlowUseCase,

            getSearchNoteUseCase = getSearchNoteUseCase,
            setSearchNoteUseCase = setSearchNoteUseCase,

            upsertNoteUseCase = upsertNoteUseCase,
            setCurrentNoteIdUseCase = setCurrentNoteIdUseCase,
        )

        // WHEN
        deletedNotesListViewModel.deletedNotesListFlow.test {
            // THEN
            awaitItem()

            val expectedListOfNotes = awaitItem()
            expectedListOfNotes[0].onItemNoteClicked()

            coVerify(exactly = 1) {
                getAllDeletedNotesFlowUseCase.invoke()
                getSearchNoteUseCase.invoke()
                setCurrentNoteIdUseCase.invoke(NOTE_ID_1)
            }

            confirmVerified(getAllDeletedNotesFlowUseCase, getSearchNoteUseCase, setCurrentNoteIdUseCase)
        }
    }

    @Test
    fun `nominal case leads to delete clicked`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { upsertNoteUseCase.invoke(firstNoteEntityRestored()) } returns NOTE_ID_1

        deletedNotesListViewModel = DeletedNotesListViewModel(
            getAllDeletedNotesFlowUseCase = getAllDeletedNotesFlowUseCase,

            getSearchNoteUseCase = getSearchNoteUseCase,
            setSearchNoteUseCase = setSearchNoteUseCase,

            upsertNoteUseCase = upsertNoteUseCase,
            setCurrentNoteIdUseCase = setCurrentNoteIdUseCase,
        )

        // WHEN
        deletedNotesListViewModel.deletedNotesListFlow.test {
            // THEN
            awaitItem()

            val expectedListOfNotes = awaitItem()
            expectedListOfNotes[0].onRestoreNoteClicked()

            coVerify(exactly = 1) {
                getAllDeletedNotesFlowUseCase.invoke()
                getSearchNoteUseCase.invoke()
//                upsertNoteUseCase.invoke(firstNoteEntityDeleted())
            }

            confirmVerified(getAllDeletedNotesFlowUseCase, getSearchNoteUseCase, upsertNoteUseCase)
        }
    }

    //endregion

    //region ================================================================= DEFAULT VALUES =================================================================

    private fun defaultAllDeletedNotesWithPicturesList() = listOf(
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
                isDeleted = true
            ), listOf()
        )
    )

    private fun firstNoteEntity() = NoteEntity(
        id = NOTE_ID_1, title = NOTE_TITLE_1, content = NOTE_CONTENT_1, date = NOTE_DATE_1, color = NOTE_COLOR_1, isFavorite = false, isDeleted = true
    )

    private fun firstNoteEntityRestored() = NoteEntity(
        id = NOTE_ID_1,
        title = NOTE_TITLE_1,
        content = NOTE_CONTENT_1,
        date = NOTE_DATE_1,
        color = NOTE_COLOR_1,
        isFavorite = false,
        isDeleted = false
    )

    private fun defaultAllDeletedNotesViewState() = listOf(
        firstDeletedNotesListViewState(), secondDeletedNotesListViewState()
    )

    private fun defaultAllDeletedNotesViewStateAfterSearchForTitleOrContent() = listOf(
        firstDeletedNotesListViewState()
    )

    private fun defaultAllDeletedNotesViewStateAfterSearchForBothTitleAndContent() = listOf(
        secondDeletedNotesListViewState()
    )

    private fun firstDeletedNotesListViewState() = DeletedNotesListViewState(
        id = NOTE_ID_1,
        title = NOTE_TITLE_1,
        content = NOTE_CONTENT_1,
        date = NOTE_FORMAT_DATE_1,
        color = NOTE_COLOR_1,
        onItemNoteClicked = EquatableCallback { },
        onRestoreNoteClicked = EquatableCallback { },
    )

    private fun secondDeletedNotesListViewState() = DeletedNotesListViewState(
        id = NOTE_ID_2,
        title = NOTE_TITLE_2,
        content = NOTE_CONTENT_2,
        date = NOTE_FORMAT_DATE_2,
        color = NOTE_COLOR_2,
        onItemNoteClicked = EquatableCallback { },
        onRestoreNoteClicked = EquatableCallback { },
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
        private const val PINK_FILTER_INT = R.string.pink
        private const val GREEN = "Green"
        private const val YELLOW = "Yellow"
        private const val PURPLE = "Purple"
        private const val BLUE = "Blue"
    }

    //endregion
}