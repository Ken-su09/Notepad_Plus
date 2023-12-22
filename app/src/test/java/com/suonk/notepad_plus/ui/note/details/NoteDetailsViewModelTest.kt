package com.suonk.notepad_plus.ui.note.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.domain.note.get_note.GetNoteByIdFlowUseCase
import com.suonk.notepad_plus.domain.note.id.GetCurrentIdFlowUseCase
import com.suonk.notepad_plus.domain.note.id.SetCurrentNoteIdUseCase
import com.suonk.notepad_plus.domain.note.upsert.UpsertNoteUseCase
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import com.suonk.notepad_plus.model.database.data.entities.NoteEntityWithPictures
import com.suonk.notepad_plus.utils.NativeText
import com.suonk.notepad_plus.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class NoteDetailsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val getNoteByIdFlowUseCase: GetNoteByIdFlowUseCase = mockk()
    private val getCurrentIdFlowUseCase: GetCurrentIdFlowUseCase = mockk()
    private val setCurrentNoteIdUseCase: SetCurrentNoteIdUseCase = mockk()
    private val upsertNoteUseCase: UpsertNoteUseCase = mockk()

    private val fixedClock = Clock.fixed(Instant.ofEpochSecond(DEFAULT_TIMESTAMP_LONG), ZoneOffset.UTC)

    private lateinit var noteDetailsViewModel: NoteDetailsViewModel

    @Before
    fun setup() {

    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        // GIVEN
        val currentIdStateFlow = MutableStateFlow(NOTE_ID_1)
        every { getCurrentIdFlowUseCase.invoke() } returns currentIdStateFlow
        every { getNoteByIdFlowUseCase.invoke(NOTE_ID_1) } returns flowOf(defaultNoteWithPictures())

        noteDetailsViewModel = NoteDetailsViewModel(
            getNoteByIdFlowUseCase = getNoteByIdFlowUseCase,
            getCurrentIdFlowUseCase = getCurrentIdFlowUseCase,
            setCurrentNoteIdUseCase = setCurrentNoteIdUseCase,
            upsertNoteUseCase = upsertNoteUseCase,
            fixedClock = fixedClock,
        )

        runCurrent()

        // THEN
        noteDetailsViewModel.noteTitle.test {
            assertEquals(NOTE_TITLE_NATIVE_TEXT_1, awaitItem())
        }
        noteDetailsViewModel.noteContent.test {
            assertEquals(NOTE_CONTENT_NATIVE_TEXT_1, awaitItem())
        }
        noteDetailsViewModel.noteColor.test {
            assertEquals(NOTE_COLOR_1, awaitItem())
        }

        verify {
            getCurrentIdFlowUseCase.invoke()
        }

        confirmVerified(getCurrentIdFlowUseCase)
    }

    @Test
    fun `initial case`() = testCoroutineRule.runTest {
        // GIVEN
        val currentIdStateFlow = MutableStateFlow(null)
        every { getCurrentIdFlowUseCase.invoke() } returns currentIdStateFlow

        noteDetailsViewModel = NoteDetailsViewModel(
            getNoteByIdFlowUseCase = getNoteByIdFlowUseCase,
            getCurrentIdFlowUseCase = getCurrentIdFlowUseCase,
            setCurrentNoteIdUseCase = setCurrentNoteIdUseCase,
            upsertNoteUseCase = upsertNoteUseCase,
            fixedClock = fixedClock
        )

        runCurrent()

        // THEN
        noteDetailsViewModel.noteTitle.test {
            assertEquals(NOTE_EMPTY_TITLE_NATIVE_TEXT, awaitItem())
        }
        noteDetailsViewModel.noteContent.test {
            assertEquals(NOTE_EMPTY_CONTENT_NATIVE_TEXT, awaitItem())
        }
        noteDetailsViewModel.noteColor.test {
            assertTrue(listOfColors().contains(awaitItem()))
        }

        verify {
            getCurrentIdFlowUseCase.invoke()
        }

        confirmVerified(getCurrentIdFlowUseCase)
    }

    @Test
    fun `user change title`() = testCoroutineRule.runTest {
        // GIVEN
        val currentIdStateFlow = MutableStateFlow(NOTE_ID_1)
        every { getCurrentIdFlowUseCase.invoke() } returns currentIdStateFlow
        every { getNoteByIdFlowUseCase.invoke(NOTE_ID_1) } returns flowOf(defaultNoteWithPictures())

        noteDetailsViewModel = NoteDetailsViewModel(
            getNoteByIdFlowUseCase = getNoteByIdFlowUseCase,
            getCurrentIdFlowUseCase = getCurrentIdFlowUseCase,
            setCurrentNoteIdUseCase = setCurrentNoteIdUseCase,
            upsertNoteUseCase = upsertNoteUseCase,
            fixedClock = fixedClock
        )

        runCurrent()

        // THEN
        noteDetailsViewModel.noteTitle.test {
            assertEquals(NOTE_TITLE_NATIVE_TEXT_1, awaitItem())
        }
        noteDetailsViewModel.noteContent.test {
            assertEquals(NOTE_CONTENT_NATIVE_TEXT_1, awaitItem())
        }
        noteDetailsViewModel.noteColor.test {
            assertEquals(NOTE_COLOR_1, awaitItem())
        }

        // THEN
        noteDetailsViewModel.onDataEvent(DEFAULT_CHANGE_TITLE)

        runCurrent()

        noteDetailsViewModel.noteTitle.test {
            assertEquals(NOTE_NEW_TITLE_NATIVE_TEXT_1, awaitItem())
        }

        verify {
            getCurrentIdFlowUseCase.invoke()
            getNoteByIdFlowUseCase.invoke(NOTE_ID_1)
        }

        confirmVerified(getCurrentIdFlowUseCase, getNoteByIdFlowUseCase)
    }

    @Test
    fun `user change content`() = testCoroutineRule.runTest {
        // GIVEN
        val currentIdStateFlow = MutableStateFlow(NOTE_ID_1)
        every { getCurrentIdFlowUseCase.invoke() } returns currentIdStateFlow
        every { getNoteByIdFlowUseCase.invoke(NOTE_ID_1) } returns flowOf(defaultNoteWithPictures())

        noteDetailsViewModel = NoteDetailsViewModel(
            getNoteByIdFlowUseCase = getNoteByIdFlowUseCase,
            getCurrentIdFlowUseCase = getCurrentIdFlowUseCase,
            setCurrentNoteIdUseCase = setCurrentNoteIdUseCase,
            upsertNoteUseCase = upsertNoteUseCase,
            fixedClock = fixedClock
        )

        runCurrent()

        // THEN
        noteDetailsViewModel.noteTitle.test {
            assertEquals(NOTE_TITLE_NATIVE_TEXT_1, awaitItem())
        }
        noteDetailsViewModel.noteContent.test {
            assertEquals(NOTE_CONTENT_NATIVE_TEXT_1, awaitItem())
        }
        noteDetailsViewModel.noteColor.test {
            assertEquals(NOTE_COLOR_1, awaitItem())
        }

        // THEN
        noteDetailsViewModel.onDataEvent(DEFAULT_CHANGE_CONTENT)

        runCurrent()

        noteDetailsViewModel.noteContent.test {
            assertEquals(NOTE_NEW_CONTENT_NATIVE_TEXT_1, awaitItem())
        }

        verify {
            getCurrentIdFlowUseCase.invoke()
            getNoteByIdFlowUseCase.invoke(NOTE_ID_1)
        }

        confirmVerified(getCurrentIdFlowUseCase, getNoteByIdFlowUseCase)
    }

    @Test
    fun `user change color`() = testCoroutineRule.runTest {
        // GIVEN
        val currentIdStateFlow = MutableStateFlow(NOTE_ID_1)
        every { getCurrentIdFlowUseCase.invoke() } returns currentIdStateFlow
        every { getNoteByIdFlowUseCase.invoke(NOTE_ID_1) } returns flowOf(defaultNoteWithPictures())

        noteDetailsViewModel = NoteDetailsViewModel(
            getNoteByIdFlowUseCase = getNoteByIdFlowUseCase,
            getCurrentIdFlowUseCase = getCurrentIdFlowUseCase,
            setCurrentNoteIdUseCase = setCurrentNoteIdUseCase,
            upsertNoteUseCase = upsertNoteUseCase,
            fixedClock = fixedClock
        )

        runCurrent()

        // THEN
        noteDetailsViewModel.noteTitle.test {
            assertEquals(NOTE_TITLE_NATIVE_TEXT_1, awaitItem())
        }
        noteDetailsViewModel.noteContent.test {
            assertEquals(NOTE_CONTENT_NATIVE_TEXT_1, awaitItem())
        }
        noteDetailsViewModel.noteColor.test {
            assertEquals(NOTE_COLOR_1, awaitItem())
        }

        // THEN
        noteDetailsViewModel.onDataEvent(DEFAULT_CHANGE_COLOR)

        runCurrent()

        // THEN
        noteDetailsViewModel.noteColor.test {
            assertEquals(NOTE_NEW_COLOR_1, awaitItem())
        }

        coVerify {
            getCurrentIdFlowUseCase.invoke()
            getNoteByIdFlowUseCase.invoke(NOTE_ID_1)
        }

        confirmVerified(getCurrentIdFlowUseCase, getNoteByIdFlowUseCase)
    }

    @Test
    fun `edit note then save`() = testCoroutineRule.runTest {
        // GIVEN
        val currentIdStateFlow = MutableStateFlow(NOTE_ID_1)
        every { getCurrentIdFlowUseCase.invoke() } returns currentIdStateFlow
        every { getNoteByIdFlowUseCase.invoke(NOTE_ID_1) } returns flowOf(defaultNoteWithPictures())
        coEvery { upsertNoteUseCase.invoke(updateNoteWithPictures()) } returns NOTE_ID_1

        noteDetailsViewModel = NoteDetailsViewModel(
            getNoteByIdFlowUseCase = getNoteByIdFlowUseCase,
            getCurrentIdFlowUseCase = getCurrentIdFlowUseCase,
            setCurrentNoteIdUseCase = setCurrentNoteIdUseCase,
            upsertNoteUseCase = upsertNoteUseCase,
            fixedClock = fixedClock,
        )

        runCurrent()

        // THEN
        noteDetailsViewModel.noteTitle.test {
            assertEquals(NOTE_TITLE_NATIVE_TEXT_1, awaitItem())
        }
        noteDetailsViewModel.noteContent.test {
            assertEquals(NOTE_CONTENT_NATIVE_TEXT_1, awaitItem())
        }
        noteDetailsViewModel.noteColor.test {
            assertEquals(NOTE_COLOR_1, awaitItem())
        }

        // THEN
        noteDetailsViewModel.onDataEvent(DEFAULT_CHANGE_TITLE)
        noteDetailsViewModel.onDataEvent(DEFAULT_CHANGE_CONTENT)
        noteDetailsViewModel.onDataEvent(DEFAULT_CHANGE_COLOR)

        runCurrent()

        noteDetailsViewModel.noteTitle.test {
            assertEquals(NOTE_NEW_TITLE_NATIVE_TEXT_1, awaitItem())
        }

        noteDetailsViewModel.noteContent.test {
            assertEquals(NOTE_NEW_CONTENT_NATIVE_TEXT_1, awaitItem())
        }
        noteDetailsViewModel.noteColor.test {
            assertEquals(NOTE_NEW_COLOR_1, awaitItem())
        }

        noteDetailsViewModel.onDataEvent(SAVE_NOTE)

        coVerify {
            getCurrentIdFlowUseCase.invoke()
            getNoteByIdFlowUseCase.invoke(NOTE_ID_1)
//            upsertNoteUseCase.invoke(updateNoteWithPictures())
        }

        confirmVerified(getCurrentIdFlowUseCase, getNoteByIdFlowUseCase, upsertNoteUseCase)
    }

    @Test
    fun `edit note to empty data then save`() = testCoroutineRule.runTest {
        // GIVEN
        val currentIdStateFlow = MutableStateFlow(NOTE_ID_1)
        every { getCurrentIdFlowUseCase.invoke() } returns currentIdStateFlow
        every { getNoteByIdFlowUseCase.invoke(NOTE_ID_1) } returns flowOf(defaultNoteWithPictures())
        coJustRun { upsertNoteUseCase.invoke(updateNoteWithPictures()) }

        noteDetailsViewModel = NoteDetailsViewModel(
            getNoteByIdFlowUseCase = getNoteByIdFlowUseCase,
            getCurrentIdFlowUseCase = getCurrentIdFlowUseCase,
            setCurrentNoteIdUseCase = setCurrentNoteIdUseCase,
            upsertNoteUseCase = upsertNoteUseCase,
            fixedClock = fixedClock,
        )

        runCurrent()

        // THEN
        noteDetailsViewModel.noteTitle.test {
            assertEquals(NOTE_TITLE_NATIVE_TEXT_1, awaitItem())
        }
        noteDetailsViewModel.noteContent.test {
            assertEquals(NOTE_CONTENT_NATIVE_TEXT_1, awaitItem())
        }
        noteDetailsViewModel.noteColor.test {
            assertEquals(NOTE_COLOR_1, awaitItem())
        }

        // THEN
        noteDetailsViewModel.onDataEvent(DEFAULT_CHANGE_TITLE_TO_EMPTY)
        noteDetailsViewModel.onDataEvent(DEFAULT_CHANGE_CONTENT_TO_EMPTY)
        noteDetailsViewModel.onDataEvent(DEFAULT_CHANGE_COLOR)

        runCurrent()

        noteDetailsViewModel.noteTitle.test {
            assertEquals(NOTE_EMPTY_TITLE_NATIVE_TEXT, awaitItem())
        }

        noteDetailsViewModel.noteContent.test {
            assertEquals(NOTE_EMPTY_CONTENT_NATIVE_TEXT, awaitItem())
        }
        noteDetailsViewModel.noteColor.test {
            assertEquals(NOTE_NEW_COLOR_1, awaitItem())
        }

        noteDetailsViewModel.onDataEvent(SAVE_NOTE)

        coVerify {
            getCurrentIdFlowUseCase.invoke()
            getNoteByIdFlowUseCase.invoke(NOTE_ID_1)
        }

        confirmVerified(getCurrentIdFlowUseCase, getNoteByIdFlowUseCase)
    }



    @Test
    fun `nominal case then delete note`() = testCoroutineRule.runTest {
        // GIVEN
        val currentIdStateFlow = MutableStateFlow(NOTE_ID_1)
        every { getCurrentIdFlowUseCase.invoke() } returns currentIdStateFlow
        every { getNoteByIdFlowUseCase.invoke(NOTE_ID_1) } returns flowOf(defaultNoteWithPictures())
//        coEvery { upsertNoteUseCase.invoke(deleteNoteWithPictures()) } returns NOTE_ID_1
        coEvery { upsertNoteUseCase.invoke(any()) } returns NOTE_ID_1

        noteDetailsViewModel = NoteDetailsViewModel(
            getNoteByIdFlowUseCase = getNoteByIdFlowUseCase,
            getCurrentIdFlowUseCase = getCurrentIdFlowUseCase,
            setCurrentNoteIdUseCase = setCurrentNoteIdUseCase,
            upsertNoteUseCase = upsertNoteUseCase,
            fixedClock = fixedClock,
        )

        runCurrent()

        // THEN
        noteDetailsViewModel.noteTitle.test {
            assertEquals(NOTE_TITLE_NATIVE_TEXT_1, awaitItem())
        }
        noteDetailsViewModel.noteContent.test {
            assertEquals(NOTE_CONTENT_NATIVE_TEXT_1, awaitItem())
        }
        noteDetailsViewModel.noteColor.test {
            assertEquals(NOTE_COLOR_1, awaitItem())
        }

        // WHEN
        noteDetailsViewModel.onDataEvent(DELETE_NOTE)

        // THEN
        coVerify {
            getCurrentIdFlowUseCase.invoke()
            getNoteByIdFlowUseCase.invoke(NOTE_ID_1)
//            upsertNoteUseCase.invoke(deleteNoteWithPictures())
//            upsertNoteUseCase.invoke(any())
        }

        confirmVerified(getCurrentIdFlowUseCase, getNoteByIdFlowUseCase, upsertNoteUseCase)
    }

    private fun defaultNoteWithPictures() = NoteEntityWithPictures(
        NoteEntity(
            id = NOTE_ID_1,
            title = NOTE_TITLE_1,
            content = NOTE_CONTENT_1,
            date = NOTE_DATE_1,
            color = NOTE_COLOR_1,
            isFavorite = false,
            isDeleted = false
        ), listOf()
    )

    private fun updateNoteWithPictures() = NoteEntity(
        id = NOTE_ID_1,
        title = NOTE_NEW_TITLE,
        content = NOTE_NEW_CONTENT_1,
        date = NOTE_DATE_1,
        color = NOTE_NEW_COLOR_1,
        isFavorite = false,
        isDeleted = false
    )

    private fun deleteNoteWithPictures() = NoteEntity(
        id = NOTE_ID_1,
        title = NOTE_NEW_TITLE,
        content = NOTE_NEW_CONTENT_1,
        date = NOTE_DATE_1,
        color = NOTE_NEW_COLOR_1,
        isFavorite = false,
        isDeleted = true
    )

    private fun defaultEmptyNoteWithPictures() = NoteEntityWithPictures(
        NoteEntity(
            id = NOTE_ID_1,
            title = EMPTY_TITLE,
            content = EMPTY_CONTENT,
            date = NOTE_DATE_1,
            color = NOTE_COLOR_1,
            isFavorite = false,
            isDeleted = false
        ), listOf()
    )

    private fun saveEmptyNoteEntity() = NoteEntity(
        id = NOTE_ID_1,
        title = EMPTY_TITLE,
        content = EMPTY_CONTENT,
        date = NOTE_DATE_1,
        color = NOTE_COLOR_1,
        isFavorite = false,
        isDeleted = false
    )

    companion object {
        private const val EMPTY_NOTE_ID = 0L
        private const val EMPTY_TITLE = ""
        private const val EMPTY_CONTENT = ""
        private val NOTE_EMPTY_TITLE_NATIVE_TEXT = NativeText.SimpleOrResource(EMPTY_TITLE, R.string.enter_a_title)
        private val NOTE_EMPTY_CONTENT_NATIVE_TEXT = NativeText.SimpleOrResource(EMPTY_CONTENT, R.string.enter_some_content)

        private const val NOTE_ID_1 = 1L
        private const val NOTE_ID_2 = 2L

        private val NOTE_TITLE_1 = "First News of the Week"
        private val NOTE_NEW_TITLE = "Second News of the Week"
        private val NOTE_TITLE_NATIVE_TEXT_1 = NativeText.SimpleOrResource("First News of the Week", R.string.enter_a_title)
        private val NOTE_NEW_TITLE_NATIVE_TEXT_1 = NativeText.SimpleOrResource("Second News of the Week", R.string.enter_a_title)
        private val NOTE_TITLE_2 = NativeText.SimpleOrResource("Je pense que Studio Jams est meilleur", R.string.enter_a_title)

        private val NOTE_CONTENT_1 =
            "Règle : \n" + "- Nombre aléatoire en 10 - 255\n" + "- Le faire 5 fois\n" + "- Tirer 5 max mangas intéressants dans chaque page (peut aller page avant et après)\n" + "- Pas le droit à la même page\n" + "- Faire un tri entre les 5 ou prendre celui qui paraît être le plus omoshiroi"
        private val NOTE_CONTENT_NATIVE_TEXT_1 = NativeText.SimpleOrResource(
            "Règle : \n" + "- Nombre aléatoire en 10 - 255\n" + "- Le faire 5 fois\n" + "- Tirer 5 max mangas intéressants dans chaque page (peut aller page avant et après)\n" + "- Pas le droit à la même page\n" + "- Faire un tri entre les 5 ou prendre celui qui paraît être le plus omoshiroi",
            R.string.enter_some_content
        )
        private val NOTE_NEW_CONTENT_1 = "Nombre aléatoire entre 15 et 2077"
        private val NOTE_NEW_CONTENT_NATIVE_TEXT_1 = NativeText.SimpleOrResource("Nombre aléatoire entre 15 et 2077", R.string.enter_some_content)

        private val NOTE_CONTENT_2 = NativeText.SimpleOrResource(
            "Très souvent, ils connaissent pas les morceaux, n'ont pour la plupart jamais joué ensemble, on leur file juste la structure du standard, on se met d'accord sur l'ordre des impros, le pont, le chorus et ça part.\n" + "\n" + "Imagine la difficulté et le niveau qu'il faut pour se coordonner à plusieurs instruments avec des gens que t'as jamais vu et avec la complexité des morceaux.\n" + "Le pianiste et le bassiste ne doivent pas se marcher dessus sur les notes basses, \n" + "Y'a des solos de folie, une écoute de malade entre chaque zicos, l'écoute des batteurs me bluffent à chaque fois\n" + "Une seule take, pas d'artifice.\n" + "\n" + "Parmi les meilleurs musiciens du monde sont passés, dont le plus grand bassiste encore en vie, Victor Wooten.",
            R.string.enter_some_content
        )

        private const val NOTE_COLOR_1 = 0xFFffab91
        private const val NOTE_NEW_COLOR_1 = 0xFF7fdeea
        private const val NOTE_COLOR_2 = 0xFFe8ed9d

        private const val DEFAULT_TIMESTAMP_LONG = 1_000_000_000L // 09/09/2001 - 01:46:40
        private val fixedClock = Clock.fixed(Instant.ofEpochSecond(DEFAULT_TIMESTAMP_LONG), ZoneOffset.UTC)
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

        private val NOTE_DATE_1 = LocalDateTime.now(fixedClock)
        private val NOTE_FORMAT_DATE_1 = NOTE_DATE_1.format(dateTimeFormatter)
        private val NOTE_DATE_2 = LocalDateTime.now(fixedClock)
        private val NOTE_FORMAT_DATE_2 = NOTE_DATE_2.format(dateTimeFormatter)

        private const val DEFAULT_EMPTY_SEARCH = ""

        private const val ENTER_TITLE = "Enter a title"
        private const val ENTER_CONTENT = "Enter some content"

        private val DEFAULT_CHANGE_TITLE = NoteDetailsDataEvent.ChangeTitle(NOTE_NEW_TITLE)
        private val DEFAULT_CHANGE_CONTENT = NoteDetailsDataEvent.ChangeContent(NOTE_NEW_CONTENT_1)

        private val DEFAULT_CHANGE_TITLE_TO_EMPTY   = NoteDetailsDataEvent.ChangeTitle(EMPTY_TITLE)
        private val DEFAULT_CHANGE_CONTENT_TO_EMPTY = NoteDetailsDataEvent.ChangeContent(EMPTY_CONTENT)
        private val DEFAULT_CHANGE_COLOR = NoteDetailsDataEvent.ChangeColor(NOTE_NEW_COLOR_1)

        private val SAVE_NOTE = NoteDetailsDataEvent.SaveNote
        private val DELETE_NOTE = NoteDetailsDataEvent.DeleteRestoreNote
        private val RESTORE_NOTE = NoteDetailsDataEvent.DeleteRestoreNote

        fun listOfColors() = listOf(0xFFffab91, 0xFFe8ed9d, 0xFFd095db, 0xFF7fdeea, 0xFFf48fb1)
    }
}