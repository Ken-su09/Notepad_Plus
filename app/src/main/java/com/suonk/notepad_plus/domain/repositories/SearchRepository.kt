package com.suonk.notepad_plus.domain.repositories

import kotlinx.coroutines.flow.StateFlow

interface SearchRepository {

    fun getCurrentSearchParametersFlow(): StateFlow<String?>
    fun setCurrentSearchParameters(search: String?)
}