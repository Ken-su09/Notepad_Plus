package com.suonk.notepad_plus.domain.filter

import com.suonk.notepad_plus.utils.Filtering
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilterRepositoryImpl @Inject constructor() : FilterRepository {
    private val currentFilterParameterFlow = MutableStateFlow(Filtering.REMOVE_FILTER)

    override fun getCurrentFilterParameterFlow(): StateFlow<Filtering> {
        return currentFilterParameterFlow
    }

    override fun setCurrentFilterParametersFlow(filtering: Filtering) {
        currentFilterParameterFlow.tryEmit(filtering)
    }
}