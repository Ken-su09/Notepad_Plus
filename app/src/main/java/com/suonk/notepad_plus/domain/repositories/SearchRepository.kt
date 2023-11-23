package com.suonk.notepad_plus.domain.repositories

import com.suonk.notepad_plus.utils.Sorting
import kotlinx.coroutines.flow.StateFlow

interface SearchRepository {

    fun getCurrentSearchParametersFlow(): StateFlow<String?>
    fun setCurrentSearchParameters(search: String?)

    fun getCurrentSortFilterParametersFlow(): StateFlow<Int>

    fun getCurrentSortParameterFlow(): StateFlow<Sorting>
    fun setCurrentSortFilterParametersFlow(itemId: Int)
}