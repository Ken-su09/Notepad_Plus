package com.suonk.notepad_plus.model.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.domain.search.SearchRepositoryImpl
import com.suonk.notepad_plus.utils.Sorting
import com.suonk.notepad_plus.utils.TestCoroutineRule
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchRepositoryImplTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val searchRepositoryImpl = SearchRepositoryImpl()

    @Before
    fun setup() {
    }

    @Test
    fun `test get current search parameters flow`() = testCoroutineRule.runTest {
        // GIVEN
        searchRepositoryImpl.setCurrentSearchParameters(DEFAULT_SEARCH)

        searchRepositoryImpl.getCurrentSearchParametersFlow().test {
            assertEquals(DEFAULT_SEARCH, awaitItem())
        }
    }

    @Test
    fun `test get current sort parameters flow`() = testCoroutineRule.runTest {
        // GIVEN
        searchRepositoryImpl.setCurrentSortFilterParametersFlow(DATE_ASC_RES)

        searchRepositoryImpl.getCurrentSortParameterFlow().test {
            assertEquals(DATE_ASC_SORTING, awaitItem())
        }
    }

    @Test
    fun `test get current sort filter parameters flow`() = testCoroutineRule.runTest {
        // GIVEN
        searchRepositoryImpl.setCurrentSortFilterParametersFlow(PINK_RES)

        searchRepositoryImpl.getCurrentSortFilterParametersFlow().test {
            assertEquals(PINK_RES, awaitItem())
        }
    }

    companion object {
        private const val DEFAULT_SEARCH = "DEFAULT_SEARCH"

        private const val DATE_ASC_RES = R.string.date_asc
        private val DATE_ASC_SORTING = Sorting.DATE_ASC

        private const val PINK_RES = R.string.pink
    }
}