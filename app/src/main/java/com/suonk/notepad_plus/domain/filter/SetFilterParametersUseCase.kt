package com.suonk.notepad_plus.domain.filter

import com.suonk.notepad_plus.utils.Filtering
import javax.inject.Inject

class SetFilterParametersUseCase @Inject constructor(private val filterRepository: FilterRepository) {

    fun invoke(filtering: Filtering) {
        filterRepository.setCurrentFilterParametersFlow(filtering)
    }
}