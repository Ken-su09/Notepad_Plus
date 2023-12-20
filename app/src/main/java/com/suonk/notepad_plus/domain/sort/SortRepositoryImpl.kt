package com.suonk.notepad_plus.domain.sort

import com.suonk.notepad_plus.utils.Sorting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SortRepositoryImpl @Inject constructor() : SortRepository {
    private val currentSortParameterFlow = MutableStateFlow(Sorting.DATE_ASC)

    override fun getCurrentSortParameterFlow(): StateFlow<Sorting> {
        return currentSortParameterFlow
    }

    override fun setCurrentSortingParametersFlow(sorting: Sorting) {
        currentSortParameterFlow.tryEmit(Sorting.DATE_ASC)
    }
}