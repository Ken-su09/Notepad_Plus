package com.suonk.notepad_plus.domain.use_cases.note.upsert

import com.suonk.notepad_plus.domain.note.upsert.UpsertNoteUseCase
import com.suonk.notepad_plus.domain.note.get_note.GetNotesRepository
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import com.suonk.notepad_plus.utils.TestCoroutineRule
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset


class UpsertNoteUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val getNotesRepository: GetNotesRepository = mockk()

    private val upsertNoteUseCase = UpsertNoteUseCase(getNotesRepository)

    @Before
    fun setup() {
    }

    @Test
    fun `initial case`() = testCoroutineRule.runTest {
        // GIVEN
        coJustRun { getNotesRepository.upsertNoteEntity(defaultNoteEntity()) }

        // WHEN
        upsertNoteUseCase.invoke(defaultNoteEntity())

        // THEN
        coVerify {
            getNotesRepository.upsertNoteEntity(defaultNoteEntity())
        }
        confirmVerified(getNotesRepository)
    }

    private fun defaultNoteEntity(): NoteEntity {
        return NoteEntity(
            id = DEFAULT_ID,
            title = DEFAULT_TITLE,
            content = DEFAULT_CONTENT,
            date = NOTE_DATE,
            color = DEFAULT_COLOR,
            isFavorite = false,
            isDeleted = false,
        )
    }

    companion object {
        private const val DEFAULT_ID = 1L
        private const val DEFAULT_TITLE = "DEFAULT_TITLE"
        private const val DEFAULT_CONTENT = "DEFAULT_CONTENT"
        private const val DEFAULT_COLOR = 0xFFffab91

        private const val DEFAULT_TIMESTAMP_LONG = 1_000_000_000L // 09/09/2001 - 01:46:40
        private val fixedClock = Clock.fixed(Instant.ofEpochSecond(DEFAULT_TIMESTAMP_LONG), ZoneOffset.UTC)

        private val NOTE_DATE = LocalDateTime.now(fixedClock)

        val colors = listOf(0xFFffab91, 0xFFe8ed9d, 0xFFd095db, 0xFF7fdeea, 0xFFf48fb1)
    }
}