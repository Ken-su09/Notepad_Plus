package com.suonk.notepad_plus.domain.sort

import com.suonk.notepad_plus.utils.Sorting
import kotlinx.coroutines.flow.StateFlow

interface SortRepository {

    fun getCurrentSortParameterFlow(): StateFlow<Sorting>
    fun setCurrentSortingParametersFlow(sorting: Sorting)
}