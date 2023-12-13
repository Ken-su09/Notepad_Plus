package com.suonk.notepad_plus.domain.use_cases.note.id

import app.cash.turbine.test
import com.suonk.notepad_plus.domain.repositories.CurrentNoteIdRepository
import com.suonk.notepad_plus.utils.TestCoroutineRule
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class GetCurrentIdFlowUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val currentNoteIdRepository: CurrentNoteIdRepository = mockk()

    private val getCurrentIdFlowUseCase = GetCurrentIdFlowUseCase(currentNoteIdRepository)

    @Before
    fun setup() {
    }

    @Test
    fun `initial case`() = testCoroutineRule.runTest {
        // GIVEN
        val currentNoteIdFlow = MutableStateFlow(currentNoteId)
        every { currentNoteIdRepository.getCurrentNoteIdFlow() } returns currentNoteIdFlow

        // WHEN
        getCurrentIdFlowUseCase.invoke().test {
            assertEquals(currentNoteId, awaitItem())

            // THEN
            verify {
                currentNoteIdRepository.getCurrentNoteIdFlow()
            }
            confirmVerified(currentNoteIdRepository)
        }
    }

    companion object {
        private const val currentNoteId = 1L
    }
}