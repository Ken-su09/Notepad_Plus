package com.suonk.notepad_plus.domain.use_cases.note.get

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.suonk.notepad_plus.domain.repositories.NoteRepository
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import com.suonk.notepad_plus.model.database.data.entities.NoteEntityWithPictures
import com.suonk.notepad_plus.utils.TestCoroutineRule
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset


class GetAllNotesFlowUseCaseTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val noteRepository: NoteRepository = mockk()

    private val getAllNotesFlowUseCase = GetAllNotesFlowUseCase(noteRepository)

    @Before
    fun setup() {
    }

    @Test
    fun `initial case`() = testCoroutineRule.runTest {
        // GIVEN
        every { noteRepository.getAllNotesWithPictures() } returns flowOf(emptyList())

        // WHEN
        getAllNotesFlowUseCase.invoke().test {
            // THEN
            assertEquals(emptyList<NoteEntityWithPictures>(), awaitItem())
            awaitComplete()

            verify {
                noteRepository.getAllNotesWithPictures()
            }

            confirmVerified(noteRepository)
        }
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        // GIVEN
        every { noteRepository.getAllNotesWithPictures() } returns flowOf(defaultAllNotesWithPicturesList())

        // WHEN
        getAllNotesFlowUseCase.invoke().test {
            // THEN
            assertEquals(defaultAllNotesWithPicturesList(), awaitItem())
            awaitComplete()

            verify {
                noteRepository.getAllNotesWithPictures()
            }

            confirmVerified(noteRepository)
        }
        // WHEN
//        getAllNotesFlowUseCase.invoke().asLiveData().observeForTesting(this) {
//            // THEN
//            assertThat(it.value).isEqualTo(defaultAllNotesWithPicturesList())
//
//            verify {
//                noteRepository.getAllNotesWithPictures()
//            }
//
//            confirmVerified(noteRepository)
//        }
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

    companion object {
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

        private val NOTE_DATE_1 = LocalDateTime.now(fixedClock)
        private val NOTE_DATE_2 = LocalDateTime.now(fixedClock)
    }
}