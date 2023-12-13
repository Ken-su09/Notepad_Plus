package com.suonk.notepad_plus.domain.use_cases.note.filter_sort

import app.cash.turbine.test
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.domain.repositories.SearchRepository
import com.suonk.notepad_plus.utils.Sorting
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

class GetSortingParametersUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val searchRepository: SearchRepository = mockk()

    private val getSortingParametersUseCase = GetSortingParametersUseCase(searchRepository)

    @Before
    fun setup() {
        val sortingValueFlow = MutableStateFlow(DEFAULT_SORTING_VALUE)
        every { searchRepository.getCurrentSortParameterFlow() } returns sortingValueFlow
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {

        getSortingParametersUseCase.invoke().test {
            TestCase.assertEquals(DEFAULT_SORTING_VALUE, awaitItem())

            verify {
                searchRepository.getCurrentSortParameterFlow()
            }
            confirmVerified(searchRepository)
        }
    }

    companion object {
        private val DEFAULT_SORTING_VALUE = Sorting.DATE_ASC
    }
}