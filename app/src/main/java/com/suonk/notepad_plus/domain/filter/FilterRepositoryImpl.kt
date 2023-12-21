package com.suonk.notepad_plus.domain.filter

import com.suonk.notepad_plus.utils.Filtering
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilterRepositoryImpl @Inject constructor() : FilterRepository {
    private val currentFilterParameterFlow = MutableStateFlow(FilterEntity.REMOVE_FILTER)

    override fun getCurrentFilterParameterFlow(): StateFlow<FilterEntity> {
        return currentFilterParameterFlow
    }

    override fun setCurrentFilterParametersFlow(filterEntity: FilterEntity) {
        currentFilterParameterFlow.tryEmit(filterEntity)
    }
}