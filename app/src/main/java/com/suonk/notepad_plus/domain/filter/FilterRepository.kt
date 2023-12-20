package com.suonk.notepad_plus.domain.filter

import com.suonk.notepad_plus.utils.Filtering
import com.suonk.notepad_plus.utils.Sorting
import kotlinx.coroutines.flow.StateFlow

interface FilterRepository {

    fun getCurrentFilterParameterFlow(): StateFlow<Filtering>

    fun setCurrentFilterParametersFlow(filtering: Filtering)
}