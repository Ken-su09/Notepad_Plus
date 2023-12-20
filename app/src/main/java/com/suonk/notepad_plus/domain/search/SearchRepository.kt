package com.suonk.notepad_plus.domain.search

import com.suonk.notepad_plus.utils.Sorting
import kotlinx.coroutines.flow.StateFlow

interface SearchRepository {

    fun getCurrentSearchParametersFlow(): StateFlow<String?>
    fun setCurrentSearchParameters(search: String?)
}