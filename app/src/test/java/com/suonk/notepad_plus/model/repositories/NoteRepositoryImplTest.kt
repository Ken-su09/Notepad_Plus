package com.suonk.notepad_plus.model.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.suonk.notepad_plus.domain.note.get_note.NoteRepositoryImpl
import com.suonk.notepad_plus.model.database.data.dao.NoteDao
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import com.suonk.notepad_plus.model.database.data.entities.NoteEntityWithPictures
import com.suonk.notepad_plus.utils.TestCoroutineRule
import io.mockk.coJustRun
import io.mockk.coVerify
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
import java.time.format.DateTimeFormatter


class NoteRepositoryImplTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val noteDao: NoteDao = mockk()

    private val noteRepositoryImpl = NoteRepositoryImpl(noteDao)

    @Before
    fun setup() {
    }

    @Test
    fun `get all notes with pictures`() = testCoroutineRule.runTest {
        every { noteDao.getAllNotesWithPictures() } returns flowOf(defaultAllNotesWithPicturesList())

        noteRepositoryImpl.getAllNotesWithPictures().test {
            assertEquals(defaultAllNotesWithPicturesList(), awaitItem())
            awaitComplete()

            verify { noteDao.getAllNotesWithPictures() }
            confirmVerified(noteDao)
        }
    }

    @Test
    fun `get all deleted notes with pictures`() = testCoroutineRule.runTest {
        every { noteDao.getAllDeletedNotesWithPictures() } returns flowOf(defaultAllDeletedNotesWithPicturesList())

        noteRepositoryImpl.getAllDeletedNotesWithPictures().test {
            assertEquals(defaultAllDeletedNotesWithPicturesList(), awaitItem())
            awaitComplete()

            verify { noteDao.getAllDeletedNotesWithPictures() }
            confirmVerified(noteDao)
        }
    }

    @Test
    fun `get note by id`() = testCoroutineRule.runTest {
        every { noteDao.getNoteById(NOTE_ID_1) } returns flowOf(firstNoteEntityWithPictures())

        noteRepositoryImpl.getNoteById(NOTE_ID_1).test {
            assertEquals(firstNoteEntityWithPictures(), awaitItem())
            awaitComplete()

            verify { noteDao.getNoteById(NOTE_ID_1) }
            confirmVerified(noteDao)
        }
    }

    @Test
    fun `update note entity`() = testCoroutineRule.runTest {
        coJustRun { noteDao.upsertNoteEntity(firstNoteEntity()) }

        noteRepositoryImpl.upsertNoteEntity(firstNoteEntity())

        coVerify { noteDao.upsertNoteEntity(firstNoteEntity()) }
        confirmVerified(noteDao)
    }

    private fun defaultAllNotesWithPicturesList() = listOf(firstNoteEntityWithPictures(), secondNoteEntityWithPictures())

    private fun defaultAllDeletedNotesWithPicturesList() = listOf(
        NoteEntityWithPictures(firstDeletedNoteEntity(), listOf()), NoteEntityWithPictures(secondDeletedNoteEntity(), listOf())
    )

    private fun firstNoteEntityWithPictures() = NoteEntityWithPictures(firstNoteEntity(), listOf())
    private fun secondNoteEntityWithPictures() = NoteEntityWithPictures(secondNoteEntity(), listOf())

    private fun firstNoteEntity() = NoteEntity(
        id = NOTE_ID_1,
        title = NOTE_TITLE_1,
        content = NOTE_CONTENT_1,
        date = NOTE_DATE_1,
        color = NOTE_COLOR_1,
        isFavorite = false,
        isDeleted = false
    )

    private fun secondNoteEntity() = NoteEntity(
        id = NOTE_ID_2,
        title = NOTE_TITLE_2,
        content = NOTE_CONTENT_2,
        date = NOTE_DATE_2,
        color = NOTE_COLOR_2,
        isFavorite = false,
        isDeleted = false
    )

    private fun firstDeletedNoteEntity() = NoteEntity(
        id = NOTE_ID_1,
        title = NOTE_TITLE_1,
        content = NOTE_CONTENT_1,
        date = NOTE_DATE_1,
        color = NOTE_COLOR_1,
        isFavorite = false,
        isDeleted = false
    )

    private fun secondDeletedNoteEntity() = NoteEntity(
        id = NOTE_ID_2,
        title = NOTE_TITLE_2,
        content = NOTE_CONTENT_2,
        date = NOTE_DATE_2,
        color = NOTE_COLOR_2,
        isFavorite = false,
        isDeleted = false
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
    }
}