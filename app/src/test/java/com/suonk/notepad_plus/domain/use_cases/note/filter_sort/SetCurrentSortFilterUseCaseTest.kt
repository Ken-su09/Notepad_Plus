package com.suonk.notepad_plus.domain.use_cases.note.filter_sort

import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.domain.repositories.SearchRepository
import com.suonk.notepad_plus.utils.TestCoroutineRule
import io.mockk.confirmVerified
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SetCurrentSortFilterUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val searchRepository: SearchRepository = mockk()

    private val setCurrentSortFilterUseCase = SetCurrentSortFilterUseCase(searchRepository)

    @Before
    fun setup() {
        justRun { searchRepository.setCurrentSortFilterParametersFlow(DEFAULT_SORT_FILTER_PARAMETER) }
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        setCurrentSortFilterUseCase.invoke(DEFAULT_SORT_FILTER_PARAMETER)

        verify {
            searchRepository.setCurrentSortFilterParametersFlow(DEFAULT_SORT_FILTER_PARAMETER)
        }

        confirmVerified(searchRepository)
    }

    companion object {
        private const val DEFAULT_SORT_FILTER_PARAMETER = R.string.pink
    }
}