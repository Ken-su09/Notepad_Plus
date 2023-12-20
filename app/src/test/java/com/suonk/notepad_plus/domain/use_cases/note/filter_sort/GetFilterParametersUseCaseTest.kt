package com.suonk.notepad_plus.domain.use_cases.note.filter_sort

import app.cash.turbine.test
import com.suonk.notepad_plus.domain.search.SearchRepository
import com.suonk.notepad_plus.utils.TestCoroutineRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.domain.filter.GetFilterParametersUseCase
import io.mockk.confirmVerified
import io.mockk.verify
import junit.framework.TestCase.assertEquals


class GetFilterParametersUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val searchRepository: SearchRepository = mockk()

    private val getFilterParametersUseCase = GetFilterParametersUseCase(searchRepository)

    @Before
    fun setup() {
        val filterValueFlow = MutableStateFlow(REMOVE_FILTER)
        every { searchRepository.getCurrentSortFilterParametersFlow() } returns filterValueFlow
    }

    @Test
    fun `nominal case`() = testCoroutineRule.runTest {

        getFilterParametersUseCase.invoke().test {
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