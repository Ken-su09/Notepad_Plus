package com.suonk.notepad_plus.domain.use_cases.note.id

import app.cash.turbine.test
import com.suonk.notepad_plus.domain.note.id.GetCurrentIdFlowChannelUseCase
import com.suonk.notepad_plus.domain.note.id.CurrentNoteIdRepository
import com.suonk.notepad_plus.utils.TestCoroutineRule
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class GetCurrentIdFlowChannelUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val currentNoteIdRepository: CurrentNoteIdRepository = mockk()

    private val getCurrentIdFlowChannelUseCase = GetCurrentIdFlowChannelUseCase(currentNoteIdRepository)

    @Before
    fun setup() {
    }

    @Test
    fun `initial case`() = testCoroutineRule.runTest {
        // GIVEN
        every { currentNoteIdRepository.getCurrentNoteIdChannel() } returns currentNoteIdChannel

        // WHEN
        getCurrentIdFlowChannelUseCase.invoke().test {
            // THEN
            expectNoEvents()
            verify {
                currentNoteIdRepository.getCurrentNoteIdChannel()
            }
            confirmVerified(currentNoteIdRepository)
        }
    }

    companion object {
        private val currentNoteIdChannel = Channel<Long?>(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    }
}