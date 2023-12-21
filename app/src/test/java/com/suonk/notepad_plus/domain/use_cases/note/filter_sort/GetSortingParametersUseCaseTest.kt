package com.suonk.notepad_plus.domain.use_cases.note.filter_sort

import app.cash.turbine.test
import com.suonk.notepad_plus.domain.sort.GetSortingParametersUseCase
import com.suonk.notepad_plus.domain.sort.SortRepository
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

    private val sortRepository: SortRepository = mockk()

    private val getSortingParametersUseCase = GetSortingParametersUseCase(sortRepository)

    @Before
    fun setup() {
        val sortingValueFlow = MutableStateFlow(DATE_ASC_SORT)
        every { sortRepository.getCurrentSortParameterFlow() } returns sortingValueFlow
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        // WHEN
        getSortingParametersUseCase.invoke().test {
            // THEN
            TestCase.assertEquals(DATE_ASC_SORT, awaitItem())

            verify {
                sortRepository.getCurrentSortParameterFlow()
            }
            confirmVerified(sortRepository)
        }
    }
    @Test
    fun `nominal case 2`() = testCoroutineRule.runTest {
        // GIVEN
        val sortingValueFlow = MutableStateFlow(COLOR_ASC_SORT)
        every { sortRepository.getCurrentSortParameterFlow() } returns sortingValueFlow

        // WHEN
        getSortingParametersUseCase.invoke().test {
            // THEN
            TestCase.assertEquals(COLOR_ASC_SORT, awaitItem())

            verify {
                sortRepository.getCurrentSortParameterFlow()
            }
            confirmVerified(sortRepository)
        }
    }

    companion object {
        private val DATE_ASC_SORT = Sorting.DATE_ASC
        private val COLOR_ASC_SORT = Sorting.COLOR_ASC
    }
}