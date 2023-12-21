package com.suonk.notepad_plus.domain.use_cases.note.filter_sort

import com.suonk.notepad_plus.domain.filter.FilterRepository
import com.suonk.notepad_plus.domain.filter.SetFilterParametersUseCase
import com.suonk.notepad_plus.utils.Filtering
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

    private val filterRepository: FilterRepository = mockk()

    private val setFilterParametersUseCase = SetFilterParametersUseCase(filterRepository)

    @Before
    fun setup() {
        justRun { filterRepository.setCurrentFilterParametersFlow(REMOVE_FILTER_PARAMETER) }
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        setFilterParametersUseCase.invoke(REMOVE_FILTER_PARAMETER)

        verify {
            filterRepository.setCurrentFilterParametersFlow(REMOVE_FILTER_PARAMETER)
        }

        confirmVerified(filterRepository)
    }

    @Test
    fun `nominal case 2`() = testCoroutineRule.runTest {
        justRun { filterRepository.setCurrentFilterParametersFlow(PINK_FILTER_PARAMETER) }
        setFilterParametersUseCase.invoke(PINK_FILTER_PARAMETER)

        verify {
            filterRepository.setCurrentFilterParametersFlow(PINK_FILTER_PARAMETER)
        }

        confirmVerified(filterRepository)
    }

    companion object {
        private val REMOVE_FILTER_PARAMETER = Filtering.REMOVE_FILTER
        private val PINK_FILTER_PARAMETER = Filtering.PINK
    }
}