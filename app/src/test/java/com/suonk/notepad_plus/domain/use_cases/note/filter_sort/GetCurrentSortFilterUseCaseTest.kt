package com.suonk.notepad_plus.domain.use_cases.note.filter_sort

import app.cash.turbine.test
import com.suonk.notepad_plus.domain.repositories.NoteRepository
import com.suonk.notepad_plus.domain.repositories.SearchRepository
import com.suonk.notepad_plus.utils.TestCoroutineRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.suonk.notepad_plus.R
import io.mockk.confirmVerified
import io.mockk.verify
import junit.framework.TestCase.assertEquals


class GetCurrentSortFilterUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val searchRepository: SearchRepository = mockk()

    private val getCurrentSortFilterUseCase = GetCurrentSortFilterUseCase(searchRepository)

    @Before
    fun setup() {
        val filterValueFlow = MutableStateFlow(REMOVE_FILTER)
        every { searchRepository.getCurrentSortFilterParametersFlow() } returns filterValueFlow
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {

        getCurrentSortFilterUseCase.invoke().test {
            assertEquals(REMOVE_FILTER, awaitItem())

            verify {
                searchRepository.getCurrentSortFilterParametersFlow()
            }
            confirmVerified(searchRepository)
        }
    }

    companion object {
        private const val REMOVE_FILTER = R.string.remove_filter
    }
}