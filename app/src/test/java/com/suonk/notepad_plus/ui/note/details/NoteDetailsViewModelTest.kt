package com.suonk.notepad_plus.ui.note.details

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.domain.use_cases.note.get.GetNoteByIdFlowUseCase
import com.suonk.notepad_plus.domain.use_cases.note.id.GetCurrentIdFlowUseCase
import com.suonk.notepad_plus.domain.use_cases.note.id.SetCurrentNoteIdUseCase
import com.suonk.notepad_plus.domain.use_cases.note.upsert.UpsertNoteUseCase
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import com.suonk.notepad_plus.model.database.data.entities.NoteEntityWithPictures
import com.suonk.notepad_plus.ui.note.list.NotesListViewModelTest
import com.suonk.notepad_plus.ui.note.list.NotesListViewState
import com.suonk.notepad_plus.utils.EquatableCallback
import com.suonk.notepad_plus.utils.NativeText
import com.suonk.notepad_plus.utils.TestCoroutineRule
import com.suonk.notepad_plus.utils.observeForTesting
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
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
    private val fixedClock: Clock = mockk()
    private val application: Application = mockk()

    private val noteDetailsViewModel = NoteDetailsViewModel(
        getNoteByIdFlowUseCase = getNoteByIdFlowUseCase,
        getCurrentIdFlowUseCase = getCurrentIdFlowUseCase,
        setCurrentNoteIdUseCase = setCurrentNoteIdUseCase,
        upsertNoteUseCase = upsertNoteUseCase,
        fixedClock = fixedClock,
        application = application,
    )

    @Before
    fun setup() {

    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        // GIVEN
        val currentIdStateFlow = MutableStateFlow(NOTE_ID_1)
        every { getCurrentIdFlowUseCase.invoke() } returns currentIdStateFlow
        every { getNoteByIdFlowUseCase.invoke(NOTE_ID_1) } returns flowOf(defaultNoteWithPictures())
        every { application.getString(R.string.enter_a_title) } returns "Enter a title"
        every { application.getString(R.string.enter_some_content) } returns "Enter some content"

        noteDetailsViewModel.noteTitle.test {
            // THEN
            assertEquals(NOTE_TITLE_1, awaitItem())
            awaitComplete()

            verify {
                getCurrentIdFlowUseCase.invoke()
                getNoteByIdFlowUseCase.invoke(NOTE_ID_1)
            }

            confirmVerified(getCurrentIdFlowUseCase, getNoteByIdFlowUseCase)
        }
        noteDetailsViewModel.noteContent.test {
            // THEN
            assertEquals(NOTE_CONTENT_1, awaitItem())
            awaitComplete()

            verify {
                getCurrentIdFlowUseCase.invoke()
                getNoteByIdFlowUseCase.invoke(NOTE_ID_1)
            }

            confirmVerified(getCurrentIdFlowUseCase, getNoteByIdFlowUseCase)
        }
        noteDetailsViewModel.noteColor.test {
            // THEN
            assertEquals(NOTE_COLOR_1, awaitItem())
            awaitComplete()

            verify {
                getCurrentIdFlowUseCase.invoke()
                getNoteByIdFlowUseCase.invoke(NOTE_ID_1)
            }

            confirmVerified(getCurrentIdFlowUseCase, getNoteByIdFlowUseCase)
        }
    }

    @Test
    fun `initial case`() = testCoroutineRule.runTest {
        // GIVEN
//        val currentIdStateFlow: StateFlow<>= flowOf()
//        every { getCurrentIdFlowUseCase.invoke() } returns flowOf(defaultNoteWithPictures())
//        every { getNoteByIdFlowUseCase.invoke() } returns flowOf(defaultEmptyNoteDetailsViewState())
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

    private fun defaultEmptyNoteDetailsViewState() = NoteDetailsViewState(
        id = EMPTY_NOTE_ID,
        title = EMPTY_TITLE,
        content = EMPTY_CONTENT,
        color = NOTE_COLOR_1,
        dateText = NativeText.Argument(R.string.last_update, NOTE_FORMAT_DATE_1),
        dateValue = ZonedDateTime.now(fixedClock).toInstant(),
        actions = emptyList(),
    )

    private fun defaultNoteDetailsViewState() = NoteDetailsViewState(
        id = NOTE_ID_1,
        title = NOTE_TITLE_1,
        content = NOTE_CONTENT_1,
        color = NOTE_COLOR_1,
        dateText = NativeText.Argument(R.string.last_update, NOTE_FORMAT_DATE_2),
        dateValue = ZonedDateTime.now(fixedClock).toInstant(),
        actions = emptyList(),
    )

    companion object {
        private const val EMPTY_NOTE_ID = 0L
        private const val EMPTY_TITLE = ""
        private const val EMPTY_CONTENT = ""

        private const val NOTE_ID_1 = 1L
        private const val NOTE_ID_2 = 2L

        private const val NOTE_TITLE_1 = "First News of the Week"
        private const val NOTE_TITLE_2 = "Je pense que Studio Jams est meilleur"

        private const val NOTE_CONTENT_1 =
            "Règle : \n" + "- Nombre aléatoire en 10 - 255\n" + "- Le faire 5 fois\n" + "- Tirer 5 max mangas intéressants dans chaque page (peut aller page avant et après)\n" + "- Pas le droit à la même page\n" + "- Faire un tri entre les 5 ou prendre celui qui paraît être le plus omoshiroi"
        private const val NOTE_CONTENT_2 =
            "Très souvent, ils connaissent pas les morceaux, n'ont pour la plupart jamais joué ensemble, on leur file juste la structure du standard, on se met d'accord sur l'ordre des impros, le pont, le chorus et ça part.\n" + "\n" + "Imagine la difficulté et le niveau qu'il faut pour se coordonner à plusieurs instruments avec des gens que t'as jamais vu et avec la complexité des morceaux.\n" + "Le pianiste et le bassiste ne doivent pas se marcher dessus sur les notes basses, \n" + "Y'a des solos de folie, une écoute de malade entre chaque zicos, l'écoute des batteurs me bluffent à chaque fois\n" + "Une seule take, pas d'artifice.\n" + "\n" + "Parmi les meilleurs musiciens du monde sont passés, dont le plus grand bassiste encore en vie, Victor Wooten."

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