package com.suonk.notepad_plus.domain.use_cases.note.search

import com.suonk.notepad_plus.domain.repositories.SearchRepository
import com.suonk.notepad_plus.utils.TestCoroutineRule
import io.mockk.confirmVerified
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SetSearchNoteUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val searchRepository: SearchRepository = mockk()

    private val setSearchNoteUseCase = SetSearchNoteUseCase(searchRepository)

    @Before
    fun setup() {
    }

    @Test
    fun `initial case`() = testCoroutineRule.runTest {
        // GIVEN
        justRun { searchRepository.setCurrentSearchParameters(DEFAULT_SEARCH) }

        // WHEN
        setSearchNoteUseCase.invoke(DEFAULT_SEARCH)

        // THEN
        verify {
            searchRepository.setCurrentSearchParameters(DEFAULT_SEARCH)
        }
        confirmVerified(searchRepository)
    }

    companion object {
        private const val DEFAULT_SEARCH = "DEFAULT_SEARCH"
    }
}