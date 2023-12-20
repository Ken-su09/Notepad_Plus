package com.suonk.notepad_plus.model.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.suonk.notepad_plus.domain.note.id.CurrentNoteIdRepositoryImpl
import com.suonk.notepad_plus.utils.TestCoroutineRule
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class CurrentNoteIdRepositoryImplTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val currentNoteIdRepositoryImpl = CurrentNoteIdRepositoryImpl()

    @Before
    fun setup() {
    }

    @Test
    fun `get current note id`() = testCoroutineRule.runTest {
        // GIVEN

        // WHEN
        currentNoteIdRepositoryImpl.setCurrentNoteIdFlow(DEFAULT_ID)

        // THEN
        currentNoteIdRepositoryImpl.getCurrentNoteIdFlow().test {
            assertEquals(DEFAULT_ID, awaitItem())
        }
    }

    @Test
    fun `get channel`() = testCoroutineRule.runTest {
        // GIVEN

        // WHEN
        currentNoteIdRepositoryImpl.setCurrentNoteIdFlow(DEFAULT_ID)

        // THEN
        val receivedValue = currentNoteIdRepositoryImpl.getCurrentNoteIdChannel().receive()
        assertEquals(DEFAULT_ID, receivedValue)
    }

    companion object {
        private const val DEFAULT_ID = 1L
        private val currentNoteIdChannel = Channel<Long?>(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    }
}