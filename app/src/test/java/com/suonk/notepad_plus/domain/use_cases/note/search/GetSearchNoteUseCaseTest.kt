package com.suonk.notepad_plus.domain.use_cases.note.search

import app.cash.turbine.test
import com.suonk.notepad_plus.domain.search.GetSearchNoteUseCase
import com.suonk.notepad_plus.domain.search.SearchRepository
import com.suonk.notepad_plus.utils.TestCoroutineRule
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class GetSearchNoteUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val searchRepository: SearchRepository = mockk()

    private val getSearchNoteUseCase = GetSearchNoteUseCase(searchRepository)

    @Before
    fun setup() {
    }

    @Test
    fun `initial case`() = testCoroutineRule.runTest {
        // GIVEN
        val currentNoteIdFlow = MutableStateFlow(DEFAULT_SEARCH)
        every { searchRepository.getCurrentSearchParametersFlow() } returns currentNoteIdFlow

        // WHEN
        getSearchNoteUseCase.invoke().test {
            TestCase.assertEquals(DEFAULT_SEARCH, awaitItem())

            // THEN
            verify {searchRepository.getCurrentSearchParametersFlow()
            }
            confirmVerified(searchRepository)
        }
    }

    companion object {
        private const val DEFAULT_SEARCH = "DEFAULT_SEARCH"
    }
}