package com.suonk.notepad_plus.domain.use_cases.note.id

import com.suonk.notepad_plus.domain.note.id.SetCurrentNoteIdUseCase
import com.suonk.notepad_plus.domain.note.id.CurrentNoteIdRepository
import com.suonk.notepad_plus.utils.TestCoroutineRule
import io.mockk.confirmVerified
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class SetCurrentNoteIdUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val currentNoteIdRepository: CurrentNoteIdRepository = mockk()

    private val setCurrentNoteIdUseCase = SetCurrentNoteIdUseCase(currentNoteIdRepository)

    @Before
    fun setup() {
    }

    @Test
    fun `initial case`() = testCoroutineRule.runTest {
        // GIVEN
        justRun { currentNoteIdRepository.setCurrentNoteIdFlow(currentNoteId) }

        // WHEN
        setCurrentNoteIdUseCase.invoke(currentNoteId)

        // THEN
        verify {
            currentNoteIdRepository.setCurrentNoteIdFlow(currentNoteId)
        }
        confirmVerified(currentNoteIdRepository)
    }

    companion object {
        private const val currentNoteId = 1L
    }
}