package com.suonk.notepad_plus.model.repositories

import com.suonk.notepad_plus.domain.repositories.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor() : SearchRepository {

    private val currentSearchParameterFlow = MutableStateFlow<String?>(null)

    override fun getCurrentSearchParametersFlow(): StateFlow<String?> = currentSearchParameterFlow

    override fun setCurrentSearchParameters(search: String?) {
        currentSearchParameterFlow.tryEmit(search)
    }
}