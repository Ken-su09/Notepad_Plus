package com.suonk.notepad_plus.domain.use_cases.note.filter_sort

import app.cash.turbine.test
import com.suonk.notepad_plus.utils.TestCoroutineRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.suonk.notepad_plus.domain.filter.FilterRepository
import com.suonk.notepad_plus.domain.filter.GetFilterParametersUseCase
import com.suonk.notepad_plus.utils.Filtering
import io.mockk.confirmVerified
import io.mockk.verify
import junit.framework.TestCase.assertEquals

class GetFilterParametersUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val filterRepository: FilterRepository = mockk()

    private val getFilterParametersUseCase = GetFilterParametersUseCase(filterRepository)

    @Before
    fun setup() {
        val filterValueFlow = MutableStateFlow(REMOVE_FILTER)
        every { filterRepository.getCurrentFilterParameterFlow() } returns filterValueFlow
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {
        // WHEN
        getFilterParametersUseCase.invoke().test {
            // THEN
            assertEquals(REMOVE_FILTER, awaitItem())

            verify {
                filterRepository.getCurrentFilterParameterFlow()
            }
            confirmVerified(filterRepository)
        }
    }

    @Test
    fun `nominal case 2`() = testCoroutineRule.runTest {
        // GIVEN
        val filterValueFlow = MutableStateFlow(REMOVE_FILTER)
        every { filterRepository.getCurrentFilterParameterFlow() } returns filterValueFlow

        // WHEN
        getFilterParametersUseCase.invoke().test {
            // THEN
            assertEquals(PINK_FILTER, awaitItem())

            verify {
                filterRepository.getCurrentFilterParameterFlow()
            }
            confirmVerified(filterRepository)
        }
    }

    companion object {
        private val REMOVE_FILTER = Filtering.REMOVE_FILTER
        private val PINK_FILTER = Filtering.PINK
    }
}